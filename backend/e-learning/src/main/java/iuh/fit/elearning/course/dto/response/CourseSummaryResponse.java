package iuh.fit.elearning.course.dto.response;

import iuh.fit.elearning.course.entity.enums.CourseLevel;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Khóa học dạng rút gọn cho danh sách (Home, Search, danh mục, Saved, hồ sơ giảng viên...).
 * saved / owned chỉ có khi gọi kèm access token.
 */
@Builder
public record CourseSummaryResponse(
        UUID id,
        String title,
        String slug,
        String thumbnailUrl,
        InstructorBrief instructor,
        CategoryBrief category,
        CourseLevel level,
        BigDecimal originalPrice,
        BigDecimal salePrice,
        int discountPercent,
        BigDecimal avgRating,
        int reviewCount,
        int studentCount,
        int totalLessons,
        int totalDurationSeconds,
        boolean bestSeller,
        CourseStatus status,
        Boolean saved,
        Boolean owned
) {
}
