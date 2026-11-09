package iuh.fit.elearning.promotion.dto.request;

import iuh.fit.elearning.promotion.entity.enums.BannerLinkType;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record BannerRequest(
        @NotBlank(message = "Tiêu đề không được để trống")
        @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự")
        String title,
        @Size(max = 255, message = "Phụ đề tối đa 255 ký tự")
        String subtitle,
        @NotBlank(message = "Ảnh banner không được để trống")
        String imageUrl,
        @Size(max = 50, message = "Nhãn nút tối đa 50 ký tự")
        String actionLabel,
        BannerLinkType linkType,
        String linkValue,
        @NotNull(message = "Vị trí hiển thị là bắt buộc")
        BannerPosition position,
        Integer sortOrder,
        Instant startAt,
        Instant endAt,
        Boolean active
) {
}
