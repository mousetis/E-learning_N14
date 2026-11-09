package iuh.fit.elearning.interaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Nội dung câu hỏi / bình luận / câu trả lời (tối đa 1000 ký tự). */
public record QaContentRequest(
        @NotBlank(message = "Nội dung không được để trống")
        @Size(max = 1000, message = "Nội dung tối đa 1000 ký tự")
        String content
) {
}
