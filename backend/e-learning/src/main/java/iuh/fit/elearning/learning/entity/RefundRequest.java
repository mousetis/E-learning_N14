package iuh.fit.elearning.learning.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.learning.entity.enums.RefundStatus;
import iuh.fit.elearning.payment.entity.Payment;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Yêu cầu hoàn tiền (trong 7 ngày, tiến độ dưới 20%).
 */
@Entity
@Table(name = "refund_requests")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class RefundRequest extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RefundStatus status = RefundStatus.PENDING;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Column(name = "processed_at")
    private Instant processedAt;
}
