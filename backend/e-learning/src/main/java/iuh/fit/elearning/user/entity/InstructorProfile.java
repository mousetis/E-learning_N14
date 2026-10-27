package iuh.fit.elearning.user.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Hồ sơ giảng viên (HoSoGiangVien).
 */
@Entity
@Table(name = "instructor_profiles")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class InstructorProfile extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "expertise")
    private String expertise;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Builder.Default
    @Column(name = "follower_count", nullable = false)
    private Integer followerCount = 0;

    @Builder.Default
    @Column(name = "student_count", nullable = false)
    private Integer studentCount = 0;

    @Builder.Default
    @Column(name = "course_count", nullable = false)
    private Integer courseCount = 0;

    @Builder.Default
    @Column(name = "avg_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal avgRating = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 0;
}
