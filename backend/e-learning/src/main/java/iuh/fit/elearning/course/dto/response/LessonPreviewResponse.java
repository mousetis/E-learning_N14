package iuh.fit.elearning.course.dto.response;

import java.time.Instant;
import java.util.UUID;

public record LessonPreviewResponse(UUID lessonId, String title, String url, Instant expiresAt) {
}
