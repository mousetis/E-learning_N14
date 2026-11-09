package iuh.fit.elearning.user.dto.response;

import iuh.fit.elearning.user.entity.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

/** UC018: hồ sơ cá nhân kèm số khóa học đã lưu / đang học / đã hoàn thành. */
public record ProfileResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        String avatarUrl,
        String coverUrl,
        String jobTitle,
        String bio,
        Instant createdAt,
        long savedCount,
        long inProgressCount,
        long completedCount
) {
}
