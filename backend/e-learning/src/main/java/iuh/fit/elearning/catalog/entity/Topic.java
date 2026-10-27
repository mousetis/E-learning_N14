package iuh.fit.elearning.catalog.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Chủ đề / Hot topic (ChuDe).
 */
@Entity
@Table(name = "topics")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Topic extends AuditableEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Builder.Default
    @Column(name = "search_count", nullable = false)
    private Integer searchCount = 0;

    @Builder.Default
    @Column(name = "is_hot", nullable = false)
    private Boolean hot = false;
}
