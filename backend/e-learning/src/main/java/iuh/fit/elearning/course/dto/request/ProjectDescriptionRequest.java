package iuh.fit.elearning.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectDescriptionRequest(
        @NotBlank(message = "Mô tả dự án không được để trống")
        @Size(max = 5000, message = "Mô tả dự án tối đa 5000 ký tự")
        String description
) {
}
