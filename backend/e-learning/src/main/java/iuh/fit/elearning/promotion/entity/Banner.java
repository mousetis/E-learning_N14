package iuh.fit.elearning.promotion.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.promotion.entity.enums.BannerLinkType;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

/**
 * Banner quảng bá trên ứng dụng.
 */
@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Banner extends AuditableEntity {

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subtitle")
    private String subtitle;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "action_label", length = 50)
    private String actionLabel;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "link_type", nullable = false, length = 20)
    private BannerLinkType linkType = BannerLinkType.NONE;

    @Column(name = "link_value", columnDefinition = "TEXT")
    private String linkValue;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 20)
    private BannerPosition position = BannerPosition.HOME;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "start_at")
    private Instant startAt;

    @Column(name = "end_at")
    private Instant endAt;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
}
