package iuh.fit.elearning.promotion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record ValidateCouponRequest(
        @NotBlank(message = "Mã giảm giá không được để trống")
        String code,
        @NotEmpty(message = "Danh sách khóa học không được để trống")
        List<UUID> courseIds
) {
}
