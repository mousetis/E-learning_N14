package iuh.fit.elearning.course.dto.response;

import iuh.fit.elearning.course.entity.enums.CourseLevel;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.interaction.dto.response.DocumentResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Chi tiết khóa học cho màn hình quản lý của giảng viên (và quản trị viên khi duyệt). */
@Builder
public record InstructorCourseDetailResponse(
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
        CourseStatus status,
        String rejectionReason,
        Instant publishedAt,
        int studentCount,
        BigDecimal avgRating,
        int reviewCount,
        int totalLessons,
        int totalDurationSeconds,
        InstructorBrief instructor,
        List<BenefitResponse> benefits,
        List<InstructorSectionResponse> sections,
        List<DocumentResponse> documents,
        String projectDescription,
        /** Các mục còn thiếu để được gửi duyệt / duyệt; rỗng nghĩa là đủ điều kiện. */
        List<String> missingForReview
) {
}
