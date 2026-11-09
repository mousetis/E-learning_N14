package iuh.fit.elearning.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangePasswordRequest(
        @NotBlank(message = "Mật khẩu hiện tại không được để trống")
        String currentPassword,
        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$",
                message = "Mật khẩu tối thiểu 8 ký tự, gồm cả chữ và số")
        String newPassword,
        /** Tùy chọn: refresh token của thiết bị hiện tại để giữ phiên này; bỏ trống thì mọi phiên bị thu hồi. */
        String refreshToken
) {
}
