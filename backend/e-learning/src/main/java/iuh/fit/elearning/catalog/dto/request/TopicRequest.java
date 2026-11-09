package iuh.fit.elearning.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TopicRequest(
        @NotBlank(message = "Tên chủ đề không được để trống")
        @Size(max = 100, message = "Tên chủ đề tối đa 100 ký tự")
        String name,
        Boolean hot
) {
}
