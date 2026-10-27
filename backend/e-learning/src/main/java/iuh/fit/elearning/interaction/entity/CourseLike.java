package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Lượt thích khóa học.
 */
@Entity
@Table(name = "course_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_id", "user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CourseLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
