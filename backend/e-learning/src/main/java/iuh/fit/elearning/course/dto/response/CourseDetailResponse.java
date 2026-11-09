package iuh.fit.elearning.course.dto.response;

import iuh.fit.elearning.course.entity.enums.CourseLevel;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * UC006: chi tiết khóa học. Các trường cá nhân hóa (owned, saved, inCart, liked, enrollment...) chỉ có khi có token.
 */
@Builder
public record CourseDetailResponse(
        UUID id,
        String title,
        String slug,
        String description,
        String thumbnailUrl,
        String introVideoUrl,
        CategoryBrief category,
        List<TopicBrief> topics,
        CourseLevel level,
        BigDecimal originalPrice,
        BigDecimal salePrice,
        int discountPercent,
        BigDecimal avgRating,
        int reviewCount,
        int studentCount,
        int likeCount,
        int totalLessons,
        int totalDurationSeconds,
        CourseStatus status,
        Instant publishedAt,
        List<BenefitResponse> benefits,
        InstructorInfoResponse instructor,
        Boolean owned,
        Boolean saved,
        Boolean inCart,
        Boolean liked,
        UUID enrollmentId,
        BigDecimal progressPercentage,
        EnrollmentStatus enrollmentStatus
) {
}
