package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Khóa học đã lưu (KhoaHocDaLuu).
 */
@Entity
@Table(name = "saved_courses",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "course_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class SavedCourse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
