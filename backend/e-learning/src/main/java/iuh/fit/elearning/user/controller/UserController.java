package iuh.fit.elearning.user.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.user.dto.request.ChangePasswordRequest;
import iuh.fit.elearning.user.dto.request.UpdateProfileRequest;
import iuh.fit.elearning.user.dto.response.ProfileResponse;
import iuh.fit.elearning.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** UC018: hồ sơ cá nhân. */
@RestController
@RequestMapping("/api/v1/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<ProfileResponse> me(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(userService.me(user.id()));
    }

    @PutMapping
    public ApiResponse<ProfileResponse> update(@AuthenticationPrincipal AuthUser user,
                                               @Valid @RequestBody UpdateProfileRequest request) {
        return ApiResponse.ok("Đã cập nhật hồ sơ", userService.update(user.id(), request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@AuthenticationPrincipal AuthUser user,
                                            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(user.id(), request);
        return ApiResponse.message("Đã đổi mật khẩu");
    }
}
