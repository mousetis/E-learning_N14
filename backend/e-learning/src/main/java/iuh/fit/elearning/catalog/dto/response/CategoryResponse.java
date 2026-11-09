package iuh.fit.elearning.catalog.dto.response;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        String iconUrl,
        String color,
        int sortOrder,
        boolean active
) {
}
