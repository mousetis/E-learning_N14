package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Câu hỏi trong khu vực Q&A của khóa học (CauHoi).
 */
@Entity
@Table(name = "qa_posts")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class QaPost extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Builder.Default
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Builder.Default
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    @Builder.Default
    @Column(name = "is_answered", nullable = false)
    private Boolean answered = false;

    @Builder.Default
    @Column(name = "is_hidden", nullable = false)
    private Boolean hidden = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
