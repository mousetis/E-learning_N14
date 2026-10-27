package iuh.fit.elearning.payment.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.payment.entity.enums.PaymentMethod;
import iuh.fit.elearning.payment.entity.enums.PaymentStatus;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Giao dịch thanh toán (ThanhToan).
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Payment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_code", length = 100)
    private String transactionCode;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "paid_at")
    private Instant paidAt;
}
