package iuh.fit.elearning.notification.dto.response;

import iuh.fit.elearning.notification.entity.enums.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        NotificationType type,
        String title,
        String content,
        UUID referenceId,
        boolean read,
        Instant readAt,
        Instant createdAt
) {
}
