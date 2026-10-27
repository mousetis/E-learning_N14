package iuh.fit.elearning.learning.entity;

import iuh.fit.elearning.common.entity.BaseEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Chứng chỉ hoàn thành khóa học (ChungChi).
 */
@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Certificate extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @Column(name = "certificate_code", nullable = false, unique = true, length = 50)
    private String certificateCode;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Builder.Default
    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt = Instant.now();
}
