package iuh.fit.elearning.notification.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.notification.dto.response.NotificationResponse;
import iuh.fit.elearning.notification.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/** UC017 - thông báo trong ứng dụng (SDS bảng 6.9). */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ApiResponse<PageResponse<NotificationResponse>> list(@AuthenticationPrincipal AuthUser user,
                                                                @RequestParam(defaultValue = "false") boolean unreadOnly,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(notificationService.list(user.id(), unreadOnly, page, size));
    }

    @GetMapping("/unread-count")
    public ApiResponse<Map<String, Long>> unreadCount(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(Map.of("count", notificationService.unreadCount(user.id())));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<NotificationResponse> markRead(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(notificationService.markRead(user.id(), id));
    }

    @PatchMapping("/read-all")
    public ApiResponse<Map<String, Integer>> markAllRead(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(Map.of("updated", notificationService.markAllRead(user.id())));
    }
}
