package iuh.fit.elearning.payment.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.config.PaymentProperties;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.enums.OrderStatus;
import iuh.fit.elearning.order.repository.OrderRepository;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import iuh.fit.elearning.payment.gateway.VnPayGateway;
import iuh.fit.elearning.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Các điểm vào công khai của luồng thanh toán: trang MOCK, return / IPN của VNPay (SDS 6.4.5). */
@Service
public class PaymentFlowService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final VnPayGateway vnPayGateway;
    private final PaymentProperties properties;

    public PaymentFlowService(OrderRepository orderRepository, PaymentRepository paymentRepository,
                              PaymentService paymentService, VnPayGateway vnPayGateway,
                              PaymentProperties properties) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.vnPayGateway = vnPayGateway;
        this.properties = properties;
    }

    // ------------------------------------------------------------------ MOCK

    @Transactional(readOnly = true)
    public String mockCheckoutPage(String orderCode) {
        Order order = requireMockOrder(orderCode);
        String note = null;
        if (order.getStatus() == OrderStatus.PAID) {
            note = "Đơn hàng đã được thanh toán.";
        } else if (order.getStatus() != OrderStatus.PENDING) {
            note = "Đơn hàng đã hết hạn hoặc không còn chờ thanh toán.";
        }
        List<PaymentHtml.Line> lines = order.getItems().stream()
                .map(i -> new PaymentHtml.Line(i.getCourseTitle(), i.getPrice())).toList();
        return PaymentHtml.mockCheckout(order.getOrderCode(), order.getTotalAmount(), lines, note);
    }

    /** Giả lập kết quả cổng thanh toán; result = SUCCESS | FAILED. */
    @Transactional
    public String completeMock(String orderCode, String result) {
        requireMockOrder(orderCode);
        boolean success = "SUCCESS".equalsIgnoreCase(result);
        if (!success && !"FAILED".equalsIgnoreCase(result)) {
            throw BusinessException.badRequest("result phải là SUCCESS hoặc FAILED");
        }
        PaymentOutcome outcome = paymentService.confirm(orderCode, success, "MOCK-" + orderCode, null,
                "MOCK:" + result.toUpperCase());
        return renderResult(outcome, orderCode);
    }

    // ------------------------------------------------------------------ VNPay

    /** Trang hiển thị cho người dùng sau khi thanh toán ở VNPay (cũng xử lý idempotent như IPN). */
    @Transactional
    public String vnpayReturn(Map<String, String> params) {
        if (!vnPayGateway.verifySignature(params)) {
            return PaymentHtml.result(false, null, "Chữ ký không hợp lệ.", properties.appRedirectUrl());
        }
        PaymentOutcome outcome = process(params);
        return renderResult(outcome, params.get("vnp_TxnRef"));
    }

    /** IPN do VNPay gọi server-to-server; trả JSON theo đặc tả VNPay. */
    @Transactional
    public Map<String, String> vnpayIpn(Map<String, String> params) {
        Map<String, String> response = new LinkedHashMap<>();
        if (!vnPayGateway.verifySignature(params)) {
            return rsp(response, "97", "Invalid signature");
        }
        PaymentOutcome outcome = process(params);
        return switch (outcome) {
            case CONFIRMED, FAILED -> rsp(response, "00", "Confirm Success");
            case ALREADY_PROCESSED -> rsp(response, "02", "Order already confirmed");
            case ORDER_NOT_FOUND -> rsp(response, "01", "Order not found");
            case AMOUNT_MISMATCH -> rsp(response, "04", "Invalid amount");
        };
    }

    // ------------------------------------------------------------------ nội bộ

    private PaymentOutcome process(Map<String, String> params) {
        String orderCode = params.get("vnp_TxnRef");
        boolean success = "00".equals(params.get("vnp_ResponseCode"))
                && (params.get("vnp_TransactionStatus") == null || "00".equals(params.get("vnp_TransactionStatus")));
        BigDecimal amount;
        try {
            amount = new BigDecimal(params.getOrDefault("vnp_Amount", "0")).movePointLeft(2);
        } catch (NumberFormatException e) {
            return PaymentOutcome.AMOUNT_MISMATCH;
        }
        return paymentService.confirm(orderCode, success, params.get("vnp_TransactionNo"), amount,
                VnPayGateway.toRaw(params));
    }

    private Order requireMockOrder(String orderCode) {
        if (!properties.mockEnabled()) {
            throw BusinessException.notFound("Không tìm thấy trang thanh toán");
        }
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy đơn hàng"));
        Payment payment = paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(order.getId()).orElse(null);
        // Chặn dùng trang giả lập để "thanh toán" đơn của cổng thật
        if (payment == null || payment.getMethod() != PaymentMethod.MOCK) {
            throw BusinessException.notFound("Không tìm thấy đơn hàng");
        }
        return order;
    }

    private String renderResult(PaymentOutcome outcome, String orderCode) {
        return switch (outcome) {
            case CONFIRMED -> PaymentHtml.result(true, orderCode,
                    "Thanh toán thành công, khóa học đã được thêm vào danh sách học của bạn.", properties.appRedirectUrl());
            case ALREADY_PROCESSED -> PaymentHtml.result(true, orderCode,
                    "Đơn hàng đã được xử lý trước đó.", properties.appRedirectUrl());
            case FAILED -> PaymentHtml.result(false, orderCode,
                    "Thanh toán không thành công hoặc đã bị hủy. Bạn có thể thử lại trong ứng dụng.",
                    properties.appRedirectUrl());
            case ORDER_NOT_FOUND -> PaymentHtml.result(false, orderCode, "Không tìm thấy đơn hàng.",
                    properties.appRedirectUrl());
            case AMOUNT_MISMATCH -> PaymentHtml.result(false, orderCode,
                    "Số tiền thanh toán không khớp với đơn hàng, vui lòng liên hệ hỗ trợ.", properties.appRedirectUrl());
        };
    }

    private static Map<String, String> rsp(Map<String, String> m, String code, String message) {
        m.put("RspCode", code);
        m.put("Message", message);
        return m;
    }
}
