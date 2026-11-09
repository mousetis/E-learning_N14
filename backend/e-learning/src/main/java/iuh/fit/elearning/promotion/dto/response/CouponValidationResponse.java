package iuh.fit.elearning.promotion.dto.response;

import java.math.BigDecimal;

/** Kết quả kiểm tra mã giảm giá cho một nhóm khóa học (UC010). */
public record CouponValidationResponse(
        String code,
        String description,
        BigDecimal subtotal,
        BigDecimal discountAmount,
        BigDecimal totalAmount
) {
}
