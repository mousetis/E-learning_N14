package iuh.fit.elearning.catalog.dto.response;

import java.util.UUID;

public record TopicResponse(
        UUID id,
        String name,
        String slug,
        int searchCount,
        boolean hot
) {
}
