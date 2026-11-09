package iuh.fit.elearning.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mã OTP không được để trống")
        @Pattern(regexp = "^\\d{4,8}$", message = "Mã OTP gồm các chữ số")
        String otp,

        @NotBlank(message = "Mật khẩu mới không được để trống")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$",
                message = "Mật khẩu tối thiểu 8 ký tự, gồm cả chữ và số")
        String newPassword
) {
}
