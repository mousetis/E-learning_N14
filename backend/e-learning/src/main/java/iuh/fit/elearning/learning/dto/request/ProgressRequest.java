package iuh.fit.elearning.learning.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProgressRequest(
        @NotNull(message = "positionSeconds là bắt buộc")
        @Min(value = 0, message = "positionSeconds không âm")
        Integer positionSeconds
) {
}
