package iuh.fit.elearning.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Mã OTP không được để trống")
        @Pattern(regexp = "^\\d{4,8}$", message = "Mã OTP gồm các chữ số")
        String otp
) {
}
