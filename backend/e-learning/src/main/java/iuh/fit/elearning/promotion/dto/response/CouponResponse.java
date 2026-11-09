package iuh.fit.elearning.promotion.dto.response;

import iuh.fit.elearning.promotion.entity.enums.DiscountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        String description,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        BigDecimal maxDiscountAmount,
        Integer usageLimit,
        int usedCount,
        Instant startAt,
        Instant endAt,
        boolean active,
        Instant createdAt
) {
}
