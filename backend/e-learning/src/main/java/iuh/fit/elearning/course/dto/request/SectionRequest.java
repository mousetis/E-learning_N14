package iuh.fit.elearning.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SectionRequest(
        @NotBlank(message = "Tên chương không được để trống")
        @Size(max = 255, message = "Tên chương tối đa 255 ký tự")
        String title,
        /** Bỏ trống: thêm vào cuối. */
        @Min(value = 1, message = "Thứ tự bắt đầu từ 1")
        Integer orderIndex
) {
}
