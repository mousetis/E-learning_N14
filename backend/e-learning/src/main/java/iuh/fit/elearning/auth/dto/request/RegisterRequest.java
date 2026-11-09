package iuh.fit.elearning.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
        String fullName,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        @Size(max = 255, message = "Email tối đa 255 ký tự")
        String email,

        @NotBlank(message = "Mật khẩu không được để trống")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$",
                message = "Mật khẩu tối thiểu 8 ký tự, gồm cả chữ và số")
        String password,

        /** Tùy chọn: nếu gửi lên thì phải trùng với mật khẩu. */
        String confirmPassword,

        /** Tùy chọn: nếu gửi lên thì phải là true. */
        Boolean acceptedTerms
) {
}
