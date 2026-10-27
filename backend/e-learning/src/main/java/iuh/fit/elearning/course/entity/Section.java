package iuh.fit.elearning.course.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Chương học (ChuongHoc).
 */
@Entity
@Table(name = "sections",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_id", "order_index"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Section extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Builder.Default
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Lesson> lessons = new ArrayList<>();
}
