package iuh.fit.elearning.user.dto.response;

import iuh.fit.elearning.user.entity.enums.UserRole;

import java.util.UUID;

/** Thông tin người dùng rút gọn (đăng nhập, tác giả bình luận, ...). */
public record UserSummaryResponse(
        UUID id,
        String fullName,
        String email,
        UserRole role,
        String avatarUrl,
        String jobTitle
) {
}
