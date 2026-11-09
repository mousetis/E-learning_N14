package iuh.fit.elearning.cart.dto.response;

import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;

import java.math.BigDecimal;

/** available = false khi khóa học đã ngừng bán (không tính vào tạm tính). */
public record CartItemResponse(
        CourseSummaryResponse course,
        BigDecimal priceAtAdded,
        boolean available,
        boolean priceChanged
) {
}
