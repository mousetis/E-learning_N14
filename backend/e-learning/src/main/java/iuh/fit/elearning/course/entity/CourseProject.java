package iuh.fit.elearning.course.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Mô tả dự án của khóa học (DuAnKhoaHoc).
 */
@Entity
@Table(name = "course_projects")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CourseProject extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false, unique = true)
    private Course course;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
}
