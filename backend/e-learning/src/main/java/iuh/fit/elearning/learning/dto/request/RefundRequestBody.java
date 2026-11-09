package iuh.fit.elearning.learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefundRequestBody(
        @NotBlank(message = "Vui lòng nhập lý do hoàn tiền")
        @Size(max = 500, message = "Lý do tối đa 500 ký tự")
        String reason
) {
}
