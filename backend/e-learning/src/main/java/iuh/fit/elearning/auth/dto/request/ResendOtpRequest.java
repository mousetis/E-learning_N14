package iuh.fit.elearning.auth.dto.request;

import iuh.fit.elearning.auth.otp.OtpPurpose;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendOtpRequest(
        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        /** REGISTER (mặc định) hoặc RESET_PASSWORD. */
        OtpPurpose purpose
) {
}
