package iuh.fit.elearning.user.dto.response;

import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record AdminUserResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        UserStatus status,
        String avatarUrl,
        String jobTitle,
        Instant createdAt,
        Instant lastLoginAt
) {
}
