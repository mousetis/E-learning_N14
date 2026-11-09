package iuh.fit.elearning.promotion.dto.response;

import iuh.fit.elearning.promotion.entity.enums.BannerLinkType;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;

import java.time.Instant;
import java.util.UUID;

public record BannerResponse(
        UUID id,
        String title,
        String subtitle,
        String imageUrl,
        String actionLabel,
        BannerLinkType linkType,
        String linkValue,
        BannerPosition position,
        int sortOrder,
        Instant startAt,
        Instant endAt,
        boolean active
) {
}
