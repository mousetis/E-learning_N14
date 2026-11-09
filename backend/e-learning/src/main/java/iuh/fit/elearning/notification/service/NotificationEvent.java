package iuh.fit.elearning.notification.service;

import iuh.fit.elearning.notification.entity.enums.NotificationType;

import java.util.Collection;
import java.util.UUID;

/**
 * Sự kiện "cần thông báo". Được phát trong transaction nghiệp vụ và xử lý SAU KHI commit
 * (SDS 3.3) để lỗi gửi thông báo không làm hỏng giao dịch chính.
 */
public record NotificationEvent(
        Collection<UUID> userIds,
        NotificationType type,
        String title,
        String content,
        UUID referenceId
) {
}
