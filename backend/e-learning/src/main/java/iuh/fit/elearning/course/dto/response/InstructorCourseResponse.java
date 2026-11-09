package iuh.fit.elearning.course.dto.response;

import iuh.fit.elearning.course.entity.enums.CourseStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Khóa học trong danh sách "Quản lý giảng dạy" của giảng viên. */
public record InstructorCourseResponse(
        UUID id,
        String title,
        String slug,
        String thumbnailUrl,
        CourseStatus status,
        String rejectionReason,
        BigDecimal originalPrice,
        BigDecimal salePrice,
        int studentCount,
        BigDecimal avgRating,
        int reviewCount,
        int totalLessons,
        int totalDurationSeconds,
        Instant publishedAt,
        Instant createdAt,
        Instant updatedAt
) {
}
