package iuh.fit.elearning.payment.gateway;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.config.PaymentProperties;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Cổng giả lập: trang HTML tự phục vụ tại /api/v1/payments/mock/{orderCode}. */
@Component
public class MockPaymentGateway implements PaymentGateway {

    private final PaymentProperties properties;
    private final String publicBaseUrl;

    public MockPaymentGateway(PaymentProperties properties, @Value("${app.public-base-url}") String publicBaseUrl) {
        this.properties = properties;
        this.publicBaseUrl = publicBaseUrl.endsWith("/") ? publicBaseUrl.substring(0, publicBaseUrl.length() - 1)
                : publicBaseUrl;
    }

    @Override
    public PaymentMethod method() {
        return PaymentMethod.MOCK;
    }

    @Override
    public String createPaymentUrl(Order order, String clientIp) {
        if (!properties.mockEnabled()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Phương thức thanh toán giả lập đang bị tắt");
        }
        return publicBaseUrl + "/api/v1/payments/mock/" + order.getOrderCode();
    }

    @Override
    public RefundResult refund(Payment payment, BigDecimal amount, String reason, String clientIp) {
        return new RefundResult(true, "Hoàn tiền giả lập thành công", "MOCK_REFUND");
    }
}
