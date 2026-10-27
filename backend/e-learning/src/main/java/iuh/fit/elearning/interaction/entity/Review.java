package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Đánh giá khóa học (DanhGia) – mỗi học viên một đánh giá cho mỗi khóa học.
 */
@Entity
@Table(name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"course_id", "user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Review extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 1000)
    private String comment;
}
