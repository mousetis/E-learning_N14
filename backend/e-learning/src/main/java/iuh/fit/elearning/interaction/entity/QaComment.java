package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Bình luận / câu trả lời cho câu hỏi Q&A (BinhLuan).
 */
@Entity
@Table(name = "qa_comments")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class QaComment extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private QaPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    @Builder.Default
    @Column(name = "is_instructor_answer", nullable = false)
    private Boolean instructorAnswer = false;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
