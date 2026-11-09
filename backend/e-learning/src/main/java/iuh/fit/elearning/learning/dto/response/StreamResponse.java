package iuh.fit.elearning.learning.dto.response;

import java.time.Instant;
import java.util.UUID;

/** Signed URL video (có thời hạn) và vị trí xem gần nhất. */
public record StreamResponse(
        UUID lessonId,
        String title,
        String url,
        Instant expiresAt,
        int lastPositionSeconds,
        int durationSeconds
) {
}
