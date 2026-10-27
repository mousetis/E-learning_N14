package iuh.fit.elearning.learning.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.course.entity.Lesson;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Tiến độ xem từng bài học (TienDoBaiHoc).
 */
@Entity
@Table(name = "lesson_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"enrollment_id", "lesson_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class LessonProgress extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Builder.Default
    @Column(name = "last_position_seconds", nullable = false)
    private Integer lastPositionSeconds = 0;

    @Builder.Default
    @Column(name = "is_completed", nullable = false)
    private Boolean completed = false;

    @Column(name = "completed_at")
    private Instant completedAt;
}
