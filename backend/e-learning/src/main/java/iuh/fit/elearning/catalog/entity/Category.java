package iuh.fit.elearning.catalog.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Danh mục khóa học (DanhMuc).
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Category extends AuditableEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "icon_url", columnDefinition = "TEXT")
    private String iconUrl;

    @Column(name = "color", length = 20)
    private String color;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
}
