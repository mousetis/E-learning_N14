package iuh.fit.elearning.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Tên danh mục không được để trống")
        @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
        String name,
        String iconUrl,
        @Size(max = 20, message = "Màu tối đa 20 ký tự")
        String color,
        Integer sortOrder,
        Boolean active
) {
}
