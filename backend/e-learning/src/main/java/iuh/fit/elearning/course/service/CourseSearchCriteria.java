package iuh.fit.elearning.course.service;

import iuh.fit.elearning.course.entity.enums.CourseLevel;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Tiêu chí tìm kiếm / lọc khóa học (UC004).
 * duration: SHORT (< 2 giờ), MEDIUM (2-10 giờ), LONG (> 10 giờ).
 * sort: popular (mặc định) | newest | rating | price_asc | price_desc.
 */
public record CourseSearchCriteria(
        String keyword,
        UUID categoryId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minRating,
        CourseLevel level,
        String duration,
        String sort
) {
}
