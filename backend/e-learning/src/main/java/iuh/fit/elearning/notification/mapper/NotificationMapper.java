package iuh.fit.elearning.notification.mapper;

import iuh.fit.elearning.notification.dto.response.NotificationResponse;
import iuh.fit.elearning.notification.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getTitle(), n.getContent(), n.getReferenceId(),
                Boolean.TRUE.equals(n.getRead()), n.getReadAt(), n.getCreatedAt());
    }
}
