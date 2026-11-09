package iuh.fit.elearning.order.dto.request;

import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

/** method bắt buộc với đơn có tiền (MOCK | VNPAY); bỏ qua khi tổng thanh toán = 0 (ghi danh ngay, FREE). */
public record CreateOrderRequest(
        @NotEmpty(message = "Danh sách khóa học không được để trống")
        List<UUID> courseIds,
        String couponCode,
        PaymentMethod method
) {
}
