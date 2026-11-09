package iuh.fit.elearning.interaction.dto.response;

import java.time.Instant;

/** Link tải có thời hạn (signed URL). */
public record DownloadResponse(String fileName, String url, Instant expiresAt) {
}
