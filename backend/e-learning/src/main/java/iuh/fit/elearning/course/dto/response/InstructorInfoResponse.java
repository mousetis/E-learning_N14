package iuh.fit.elearning.course.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

/** Thông tin giảng viên hiển thị trong chi tiết khóa học. */
@Builder
public record InstructorInfoResponse(
        UUID id,
        String fullName,
        String avatarUrl,
        String jobTitle,
        String expertise,
        int followerCount,
        int studentCount,
        int courseCount,
        BigDecimal avgRating,
        Boolean following
) {
}
