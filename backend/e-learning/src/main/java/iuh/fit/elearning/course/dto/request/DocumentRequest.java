package iuh.fit.elearning.course.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DocumentRequest(
        @NotBlank(message = "Tên tệp không được để trống")
        @Size(max = 255, message = "Tên tệp tối đa 255 ký tự")
        String fileName,
        /** Key trả về từ POST /files?type=DOCUMENT. */
        @NotBlank(message = "Tệp tài liệu không được để trống")
        String fileUrl,
        @Min(value = 0, message = "Dung lượng không âm")
        Long fileSize,
        @Size(max = 20, message = "Loại tệp tối đa 20 ký tự")
        String fileType
) {
}
