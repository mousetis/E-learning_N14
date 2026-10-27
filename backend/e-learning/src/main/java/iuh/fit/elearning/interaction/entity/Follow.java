package iuh.fit.elearning.interaction.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Học viên theo dõi giảng viên (TheoDoi).
 */
@Entity
@Table(name = "follows",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"follower_id", "instructor_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Follow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;
}
