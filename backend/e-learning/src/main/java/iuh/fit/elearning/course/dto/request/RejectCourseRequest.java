package iuh.fit.elearning.course.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectCourseRequest(
        @NotBlank(message = "Vui lòng nhập lý do từ chối")
        @Size(max = 1000, message = "Lý do tối đa 1000 ký tự")
        String reason
) {
}
