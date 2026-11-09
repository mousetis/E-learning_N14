package iuh.fit.elearning.learning.dto.response;

import iuh.fit.elearning.learning.entity.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RefundResponse(
        UUID id,
        UUID enrollmentId,
        RefundStatus status,
        BigDecimal amount,
        String reason,
        String rejectReason,
        Instant processedAt,
        Instant createdAt
) {
}
