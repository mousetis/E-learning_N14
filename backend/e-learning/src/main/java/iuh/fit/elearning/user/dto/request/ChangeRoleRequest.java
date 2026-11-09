package iuh.fit.elearning.user.dto.request;

import iuh.fit.elearning.user.entity.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
        @NotNull(message = "Vai trò là bắt buộc")
        UserRole role
) {
}
