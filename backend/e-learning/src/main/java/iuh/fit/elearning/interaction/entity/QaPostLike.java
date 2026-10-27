package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Lượt thả tim cho câu hỏi Q&A.
 */
@Entity
@Table(name = "qa_post_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id", "user_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class QaPostLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private QaPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
