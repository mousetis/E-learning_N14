package iuh.fit.elearning.payment.gateway;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.util.HashUtils;
import iuh.fit.elearning.config.PaymentProperties;
import iuh.fit.elearning.config.VnPayProperties;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * VNPay (API 2.1.0): tạo URL thanh toán ký HMAC-SHA512, xác thực chữ ký return / IPN, gọi API hoàn tiền.
 */
@Slf4j
@Component
public class VnPayGateway implements PaymentGateway {

    private static final DateTimeFormatter VNP_TIME =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));

    private final VnPayProperties properties;
    private final PaymentProperties paymentProperties;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public VnPayGateway(VnPayProperties properties, PaymentProperties paymentProperties) {
        this.properties = properties;
        this.paymentProperties = paymentProperties;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.VNPAY;
    }

    @Override
    public String createPaymentUrl(Order order, String clientIp) {
        requireConfigured();
        Instant now = Instant.now();
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", properties.tmnCode());
        params.put("vnp_Amount", order.getTotalAmount().movePointRight(2).toBigInteger().toString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", order.getOrderCode());
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getOrderCode());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", properties.returnUrl());
        params.put("vnp_IpAddr", clientIp == null || clientIp.isBlank() ? "127.0.0.1" : clientIp);
        params.put("vnp_CreateDate", VNP_TIME.format(now));
        params.put("vnp_ExpireDate", VNP_TIME.format(now.plus(paymentProperties.orderExpiryOrDefault())));

        String query = joinEncoded(params);
        String hash = HashUtils.hmacSha512Hex(properties.hashSecret(), query);
        return properties.payUrl() + "?" + query + "&vnp_SecureHash=" + hash;
    }

    /** Xác thực chữ ký của tham số vnp_* nhận từ return URL / IPN. */
    public boolean verifySignature(Map<String, String> requestParams) {
        if (!properties.configured()) {
            return false;
        }
        String received = requestParams.get("vnp_SecureHash");
        if (received == null || received.isBlank()) {
            return false;
        }
        Map<String, String> sorted = new TreeMap<>();
        requestParams.forEach((k, v) -> {
            if (k.startsWith("vnp_") && !k.equals("vnp_SecureHash") && !k.equals("vnp_SecureHashType")
                    && v != null && !v.isEmpty()) {
                sorted.put(k, v);
            }
        });
        String expected = HashUtils.hmacSha512Hex(properties.hashSecret(), joinEncoded(sorted));
        return HashUtils.constantTimeEquals(expected.toLowerCase(), received.toLowerCase());
    }

    @Override
    public RefundResult refund(Payment payment, BigDecimal amount, String reason, String clientIp) {
        requireConfigured();
        Map<String, String> raw = parseRaw(payment.getRawResponse());
        String txnNo = payment.getTransactionCode();
        String payDate = raw.get("vnp_PayDate");
        if (txnNo == null || payDate == null) {
            return new RefundResult(false, "Thiếu thông tin giao dịch gốc để hoàn tiền", null);
        }
        String requestId = UUID.randomUUID().toString().replace("-", "");
        String createDate = VNP_TIME.format(Instant.now());
        String ip = clientIp == null || clientIp.isBlank() ? "127.0.0.1" : clientIp;
        String orderInfo = "Hoan tien don hang " + payment.getOrder().getOrderCode();
        String amountStr = amount.movePointRight(2).toBigInteger().toString();
        boolean full = amount.compareTo(payment.getAmount()) == 0;
        String type = full ? "02" : "03";
        String createBy = "system";

        String data = String.join("|", requestId, "2.1.0", "refund", properties.tmnCode(), type,
                payment.getOrder().getOrderCode(), amountStr, txnNo, payDate, createBy, createDate, ip, orderInfo);
        String secureHash = HashUtils.hmacSha512Hex(properties.hashSecret(), data);

        String body = "{"
                + json("vnp_RequestId", requestId) + ","
                + json("vnp_Version", "2.1.0") + ","
                + json("vnp_Command", "refund") + ","
                + json("vnp_TmnCode", properties.tmnCode()) + ","
                + json("vnp_TransactionType", type) + ","
                + json("vnp_TxnRef", payment.getOrder().getOrderCode()) + ","
                + json("vnp_Amount", amountStr) + ","
                + json("vnp_OrderInfo", orderInfo) + ","
                + json("vnp_TransactionNo", txnNo) + ","
                + json("vnp_TransactionDate", payDate) + ","
                + json("vnp_CreateBy", createBy) + ","
                + json("vnp_CreateDate", createDate) + ","
                + json("vnp_IpAddr", ip) + ","
                + json("vnp_SecureHash", secureHash)
                + "}";
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(properties.apiUrl()))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String text = response.body();
            String code = extract(text, "vnp_ResponseCode");
            boolean ok = response.statusCode() == 200 && "00".equals(code);
            return new RefundResult(ok, ok ? "Hoàn tiền thành công" : "VNPay từ chối hoàn tiền (mã " + code + ")", text);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.PAYMENT_GATEWAY_ERROR);
        } catch (Exception e) {
            log.warn("Gọi API hoàn tiền VNPay thất bại: {}", e.getMessage());
            throw new BusinessException(ErrorCode.PAYMENT_GATEWAY_ERROR);
        }
    }

    /** Chuỗi "k=v&k=v" (đã mã hóa) dùng làm dữ liệu ký; cũng là query của URL thanh toán. */
    private static String joinEncoded(Map<String, String> sortedParams) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : sortedParams.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(e.getKey()).append('=').append(URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII));
        }
        return sb.toString();
    }

    /** Lưu tham số return/IPN đã xác thực dạng "k=v&k=v" vào payments.raw_response. */
    public static String toRaw(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        new TreeMap<>(params).forEach((k, v) -> {
            if (k.startsWith("vnp_") && !k.equals("vnp_SecureHash")) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(k).append('=').append(URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8));
            }
        });
        return sb.toString();
    }

    private static Map<String, String> parseRaw(String raw) {
        Map<String, String> map = new HashMap<>();
        if (raw == null || raw.isBlank()) {
            return map;
        }
        for (String pair : raw.split("&")) {
            int i = pair.indexOf('=');
            if (i > 0) {
                map.put(pair.substring(0, i), java.net.URLDecoder.decode(pair.substring(i + 1), StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private static String json(String key, String value) {
        return "\"" + key + "\":\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String extract(String text, String key) {
        if (text == null) {
            return null;
        }
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(text);
        return m.find() ? m.group(1) : null;
    }

    private void requireConfigured() {
        if (!properties.configured()) {
            throw new BusinessException(ErrorCode.SERVICE_UNAVAILABLE,
                    "VNPay chưa được cấu hình (VNPAY_TMN_CODE / VNPAY_HASH_SECRET)");
        }
    }
}
