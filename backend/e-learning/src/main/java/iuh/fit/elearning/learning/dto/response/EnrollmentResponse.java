package iuh.fit.elearning.learning.dto.response;

import iuh.fit.elearning.course.dto.response.CourseSummaryResponse;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Khóa học của tôi (UC012). refundEligible theo quy tắc SDS 7.8.3; refundDeadline = enrolledAt + 7 ngày. */
public record EnrollmentResponse(
        UUID enrollmentId,
        CourseSummaryResponse course,
        BigDecimal progressPercentage,
        EnrollmentStatus status,
        Instant enrolledAt,
        Instant completedAt,
        Instant lastAccessedAt,
        String certificateCode,
        boolean refundEligible,
        Instant refundDeadline
) {
}
