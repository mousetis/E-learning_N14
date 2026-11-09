package iuh.fit.elearning.interaction.dto.response;

import iuh.fit.elearning.common.response.PageResponse;

import java.math.BigDecimal;
import java.util.Map;

/** Danh sách đánh giá của khóa học kèm thống kê số sao (1..5). */
public record ReviewListResponse(
        BigDecimal avgRating,
        int reviewCount,
        Map<Integer, Long> distribution,
        PageResponse<ReviewResponse> reviews
) {
}
