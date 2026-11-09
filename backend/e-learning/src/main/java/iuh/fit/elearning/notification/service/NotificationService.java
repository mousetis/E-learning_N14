package iuh.fit.elearning.notification.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.notification.dto.response.NotificationResponse;
import iuh.fit.elearning.notification.entity.Notification;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.mapper.NotificationMapper;
import iuh.fit.elearning.notification.push.PushSender;
import iuh.fit.elearning.notification.repository.NotificationRepository;
import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.repository.UserRepository;
import iuh.fit.elearning.user.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * UC017: tạo, gửi, đọc thông báo.
 * Các module khác chỉ gọi {@link #notifyUser}/{@link #notifyUsers}; thông báo được lưu và gửi đẩy sau khi commit.
 */
@Slf4j
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PushSender pushSender;
    private final NotificationMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               SessionService sessionService, PushSender pushSender, NotificationMapper mapper,
                               ApplicationEventPublisher eventPublisher) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.sessionService = sessionService;
        this.pushSender = pushSender;
        this.mapper = mapper;
        this.eventPublisher = eventPublisher;
    }

    // ------------------------------------------------------------ phát sự kiện (gọi từ các module khác)

    public void notifyUser(UUID userId, NotificationType type, String title, String content, UUID referenceId) {
        if (userId == null) {
            return;
        }
        eventPublisher.publishEvent(new NotificationEvent(List.of(userId), type, title, content, referenceId));
    }

    public void notifyUsers(Collection<UUID> userIds, NotificationType type, String title, String content,
                            UUID referenceId) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        eventPublisher.publishEvent(new NotificationEvent(List.copyOf(userIds), type, title, content, referenceId));
    }

    // ------------------------------------------------------------ xử lý sự kiện sau khi commit

    /**
     * fallbackExecution = true: vẫn xử lý khi sự kiện được phát ngoài transaction.
     * REQUIRES_NEW: lưu thông báo trong transaction riêng sau khi transaction chính đã commit.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(NotificationEvent event) {
        try {
            List<Notification> batch = new ArrayList<>();
            for (UUID userId : event.userIds()) {
                User ref = userRepository.getReferenceById(userId);
                batch.add(Notification.builder()
                        .user(ref)
                        .type(event.type())
                        .title(event.title())
                        .content(event.content())
                        .referenceId(event.referenceId())
                        .build());
            }
            notificationRepository.saveAllAndFlush(batch);
            for (UUID userId : event.userIds()) {
                push(userId, event);
            }
        } catch (RuntimeException e) {
            // Lỗi thông báo không được ảnh hưởng nghiệp vụ chính
            log.error("Không tạo được thông báo {}: {}", event.type(), e.getMessage());
        }
    }

    private void push(UUID userId, NotificationEvent event) {
        try {
            List<String> tokens = sessionService.activeFcmTokens(userId);
            if (!tokens.isEmpty()) {
                Map<String, String> data = Map.of(
                        "type", event.type().name(),
                        "referenceId", event.referenceId() == null ? "" : event.referenceId().toString());
                pushSender.send(tokens, event.title(), event.content() == null ? "" : event.content(), data);
            }
        } catch (RuntimeException e) {
            log.warn("Gửi thông báo đẩy thất bại cho user {}: {}", userId, e.getMessage());
        }
    }

    // ------------------------------------------------------------ API cho người dùng

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> list(UUID userId, boolean unreadOnly, int page, int size) {
        PageRequest pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100));
        Page<Notification> result = unreadOnly
                ? notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId, pageable)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.of(result, mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public long unreadCount(UUID userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public NotificationResponse markRead(UUID userId, UUID notificationId) {
        Notification n = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> BusinessException.notFound("Không tìm thấy thông báo"));
        if (!Boolean.TRUE.equals(n.getRead())) {
            n.setRead(true);
            n.setReadAt(Instant.now());
        }
        return mapper.toResponse(n);
    }

    @Transactional
    public int markAllRead(UUID userId) {
        return notificationRepository.markAllRead(userId, Instant.now());
    }
}
