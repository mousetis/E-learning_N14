package iuh.fit.elearning.course.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Bài học (BaiHoc).
 */
@Entity
@Table(name = "lessons",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"section_id", "order_index"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Lesson extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "video_url", columnDefinition = "TEXT")
    private String videoUrl;

    @Builder.Default
    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds = 0;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Builder.Default
    @Column(name = "is_preview", nullable = false)
    private Boolean preview = false;
}
