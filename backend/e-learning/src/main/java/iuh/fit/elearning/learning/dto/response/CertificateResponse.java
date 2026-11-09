package iuh.fit.elearning.learning.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CertificateResponse(
        String code,
        UUID courseId,
        String courseTitle,
        String studentName,
        String instructorName,
        Instant issuedAt,
        String pdfUrl
) {
}
