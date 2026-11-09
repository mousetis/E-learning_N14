package iuh.fit.elearning.interaction.dto.response;

import java.time.Instant;
import java.util.UUID;

public record QaPostResponse(
        UUID id,
        UUID courseId,
        AuthorResponse author,
        String content,
        int likeCount,
        int commentCount,
        boolean answered,
        boolean hidden,
        Boolean liked,
        Instant createdAt
) {
}
