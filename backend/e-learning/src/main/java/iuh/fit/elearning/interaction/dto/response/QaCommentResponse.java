package iuh.fit.elearning.interaction.dto.response;

import java.time.Instant;
import java.util.UUID;

public record QaCommentResponse(
        UUID id,
        UUID postId,
        AuthorResponse author,
        String content,
        boolean instructorAnswer,
        Instant createdAt
) {
}
