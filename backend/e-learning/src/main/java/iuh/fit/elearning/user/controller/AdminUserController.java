package iuh.fit.elearning.user.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.user.dto.request.ChangeRoleRequest;
import iuh.fit.elearning.user.dto.response.AdminUserResponse;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** UC023: quản lý người dùng (ADMIN). */
@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ApiResponse<PageResponse<AdminUserResponse>> list(@RequestParam(required = false) UserRole role,
                                                             @RequestParam(required = false) UserStatus status,
                                                             @RequestParam(required = false) String keyword,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(adminUserService.list(role, status, keyword, page, size));
    }

    @PatchMapping("/{id}/lock")
    public ApiResponse<AdminUserResponse> lock(@AuthenticationPrincipal AuthUser admin, @PathVariable UUID id) {
        return ApiResponse.ok("Đã khóa tài khoản", adminUserService.lock(admin.id(), id));
    }

    @PatchMapping("/{id}/unlock")
    public ApiResponse<AdminUserResponse> unlock(@AuthenticationPrincipal AuthUser admin, @PathVariable UUID id) {
        return ApiResponse.ok("Đã mở khóa tài khoản", adminUserService.unlock(admin.id(), id));
    }

    @PatchMapping("/{id}/role")
    public ApiResponse<AdminUserResponse> role(@AuthenticationPrincipal AuthUser admin, @PathVariable UUID id,
                                               @Valid @RequestBody ChangeRoleRequest request) {
        return ApiResponse.ok("Đã cập nhật vai trò", adminUserService.changeRole(admin.id(), id, request.role()));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal AuthUser admin, @PathVariable UUID id) {
        adminUserService.delete(admin.id(), id);
        return ApiResponse.message("Đã xóa tài khoản");
    }
}
