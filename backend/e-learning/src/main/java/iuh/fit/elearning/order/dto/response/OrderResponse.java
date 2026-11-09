package iuh.fit.elearning.order.dto.response;

import iuh.fit.elearning.order.entity.enums.OrderStatus;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/** paymentUrl chỉ có khi vừa tạo đơn cần thanh toán qua cổng. */
public record OrderResponse(
        String orderCode,
        OrderStatus status,
        PaymentMethod method,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String couponCode,
        Instant expiresAt,
        Instant paidAt,
        Instant createdAt,
        List<OrderItemResponse> items,
        String paymentUrl
) {
}
