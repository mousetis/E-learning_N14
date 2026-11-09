package iuh.fit.elearning.auth.security;

import iuh.fit.elearning.user.entity.enums.UserRole;

import java.util.UUID;

/**
 * Thông tin người dùng hiện tại, đặt trong SecurityContext (principal).
 * Controller nhận bằng @AuthenticationPrincipal AuthUser; là null khi chưa đăng nhập (endpoint Public).
 */
public record AuthUser(UUID id, String email, UserRole role) {

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isInstructor() {
        return role == UserRole.INSTRUCTOR;
    }

    /** Trả về id của người dùng hoặc null nếu khách. */
    public static UUID idOf(AuthUser user) {
        return user == null ? null : user.id();
    }
}
