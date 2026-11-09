package iuh.fit.elearning.interaction.dto.response;

import java.time.Instant;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        UUID courseId,
        AuthorResponse author,
        String title,
        String description,
        String fileName,
        String fileType,
        Long fileSize,
        String fileUrl,
        String feedback,
        Instant feedbackAt,
        Instant createdAt,
        Instant updatedAt
) {
}
