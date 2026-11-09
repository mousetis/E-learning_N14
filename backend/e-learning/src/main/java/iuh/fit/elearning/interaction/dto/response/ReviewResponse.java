package iuh.fit.elearning.interaction.dto.response;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID courseId,
        String courseTitle,
        AuthorResponse author,
        int rating,
        String comment,
        Instant createdAt,
        Instant updatedAt
) {
}
