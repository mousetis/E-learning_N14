package iuh.fit.elearning.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        String password,

        /** Token thông báo đẩy của thiết bị (tùy chọn). */
        String fcmToken,

        /** Mô tả thiết bị, ví dụ "iPhone 15 / iOS 18" (tùy chọn). */
        String deviceInfo
) {
}
