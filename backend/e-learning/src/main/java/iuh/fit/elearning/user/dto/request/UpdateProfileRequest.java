package iuh.fit.elearning.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "Họ tên không được để trống")
        @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
        String fullName,
        @Size(max = 150, message = "Chức danh tối đa 150 ký tự")
        String jobTitle,
        String avatarUrl,
        String coverUrl,
        @Size(max = 2000, message = "Giới thiệu tối đa 2000 ký tự")
        String bio
) {
}
