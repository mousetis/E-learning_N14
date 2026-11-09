package iuh.fit.elearning.learning.dto.response;

import iuh.fit.elearning.course.dto.response.CurriculumResponse;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;

import java.math.BigDecimal;
import java.util.UUID;

/** Dữ liệu màn hình học (UC011). enrollmentId / progress là null khi giảng viên hoặc quản trị viên xem thử. */
public record LearnCourseResponse(
        UUID courseId,
        String title,
        UUID enrollmentId,
        BigDecimal progressPercentage,
        EnrollmentStatus status,
        String certificateCode,
        UUID resumeLessonId,
        CurriculumResponse curriculum
) {
}
