package iuh.fit.elearning.promotion.mapper;

import iuh.fit.elearning.promotion.dto.response.BannerResponse;
import iuh.fit.elearning.promotion.dto.response.CouponResponse;
import iuh.fit.elearning.promotion.entity.Banner;
import iuh.fit.elearning.promotion.entity.Coupon;
import iuh.fit.elearning.storage.service.StorageService;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    private final StorageService storageService;

    public PromotionMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    public BannerResponse toResponse(Banner b) {
        return new BannerResponse(b.getId(), b.getTitle(), b.getSubtitle(), storageService.resolveUrl(b.getImageUrl()),
                b.getActionLabel(), b.getLinkType(), b.getLinkValue(), b.getPosition(), b.getSortOrder(),
                b.getStartAt(), b.getEndAt(), Boolean.TRUE.equals(b.getActive()));
    }

    public CouponResponse toResponse(Coupon c) {
        return new CouponResponse(c.getId(), c.getCode(), c.getDescription(), c.getDiscountType(),
                c.getDiscountValue(), c.getMinOrderValue(), c.getMaxDiscountAmount(), c.getUsageLimit(),
                c.getUsedCount(), c.getStartAt(), c.getEndAt(), Boolean.TRUE.equals(c.getActive()), c.getCreatedAt());
    }
}
