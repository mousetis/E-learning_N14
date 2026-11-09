package iuh.fit.elearning.course.dto.request;

import iuh.fit.elearning.course.entity.enums.CourseLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** UC019: tạo / cập nhật thông tin khóa học. Giá bán không được lớn hơn giá gốc. */
public record CourseRequest(
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
        String title,
        @Size(max = 10000, message = "Mô tả tối đa 10000 ký tự")
        String description,
        @NotNull(message = "Danh mục là bắt buộc")
        UUID categoryId,
        List<UUID> topicIds,
        CourseLevel level,
        String thumbnailUrl,
        String introVideoUrl,
        @NotNull(message = "Giá gốc là bắt buộc")
        @DecimalMin(value = "0", message = "Giá gốc không âm")
        BigDecimal originalPrice,
        @NotNull(message = "Giá bán là bắt buộc")
        @DecimalMin(value = "0", message = "Giá bán không âm")
        BigDecimal salePrice,
        @Valid
        List<BenefitRequest> benefits
) {
    public record BenefitRequest(
            @NotBlank(message = "Nội dung lợi ích không được để trống")
            @Size(max = 255, message = "Lợi ích tối đa 255 ký tự")
            String content,
            @Size(max = 50, message = "Biểu tượng tối đa 50 ký tự")
            String icon
    ) {
    }
}
