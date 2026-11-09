package iuh.fit.elearning.interaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
        @NotBlank(message = "Nhận xét không được để trống")
        @Size(max = 2000, message = "Nhận xét tối đa 2000 ký tự")
        String feedback
) {
}
