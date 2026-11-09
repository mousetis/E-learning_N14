package iuh.fit.elearning.interaction.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.interaction.dto.response.LikeResponse;
import iuh.fit.elearning.interaction.service.SocialService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** UC016: theo dõi giảng viên; UC011: thích khóa học. */
@RestController
@RequestMapping("/api/v1")
public class SocialController {

    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/instructors/{id}/follow")
    public ApiResponse<Void> follow(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        socialService.follow(user.id(), id);
        return ApiResponse.message("Đã theo dõi giảng viên");
    }

    @DeleteMapping("/instructors/{id}/follow")
    public ApiResponse<Void> unfollow(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        socialService.unfollow(user.id(), id);
        return ApiResponse.message("Đã bỏ theo dõi giảng viên");
    }

    @PostMapping("/courses/{id}/like")
    public ApiResponse<LikeResponse> like(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(socialService.likeCourse(user, id));
    }

    @DeleteMapping("/courses/{id}/like")
    public ApiResponse<LikeResponse> unlike(@AuthenticationPrincipal AuthUser user, @PathVariable UUID id) {
        return ApiResponse.ok(socialService.unlikeCourse(user.id(), id));
    }
}
