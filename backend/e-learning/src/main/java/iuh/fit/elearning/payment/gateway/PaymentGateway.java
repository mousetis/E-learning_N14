package iuh.fit.elearning.payment.gateway;

import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.payment.entity.Payment;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;

import java.math.BigDecimal;

/** Cổng thanh toán (SDS 2.5): MOCK cho phát triển, VNPay sandbox/production. */
public interface PaymentGateway {

    PaymentMethod method();

    /** URL để ứng dụng mở thanh toán cho đơn hàng. */
    String createPaymentUrl(Order order, String clientIp);

    /** Hoàn tiền một khoản thanh toán đã PAID. */
    RefundResult refund(Payment payment, BigDecimal amount, String reason, String clientIp);

    record RefundResult(boolean success, String message, String rawResponse) {
    }
}
