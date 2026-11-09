package iuh.fit.elearning.promotion.dto.request;

import iuh.fit.elearning.promotion.entity.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;

public record CouponRequest(
        @NotBlank(message = "Mã giảm giá không được để trống")
        @Size(max = 50, message = "Mã tối đa 50 ký tự")
        @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã chỉ gồm chữ, số, gạch ngang và gạch dưới")
        String code,
        @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
        String description,
        @NotNull(message = "Loại giảm giá là bắt buộc")
        DiscountType discountType,
        @NotNull(message = "Giá trị giảm là bắt buộc")
        @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
        BigDecimal discountValue,
        @DecimalMin(value = "0", message = "Giá trị đơn tối thiểu không âm")
        BigDecimal minOrderValue,
        @DecimalMin(value = "0", message = "Mức giảm tối đa không âm")
        BigDecimal maxDiscountAmount,
        @Min(value = 1, message = "Số lượt dùng tối thiểu là 1")
        Integer usageLimit,
        @NotNull(message = "Thời gian bắt đầu là bắt buộc")
        Instant startAt,
        @NotNull(message = "Thời gian kết thúc là bắt buộc")
        Instant endAt
) {
}
