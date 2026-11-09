package iuh.fit.elearning.promotion.controller;

import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.promotion.dto.response.BannerResponse;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;
import iuh.fit.elearning.promotion.service.PromotionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** UC024: banner đang hiển thị (public). */
@RestController
@RequestMapping("/api/v1/banners")
public class BannerController {

    private final PromotionService promotionService;

    public BannerController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public ApiResponse<List<BannerResponse>> banners(@RequestParam(defaultValue = "HOME") BannerPosition position) {
        return ApiResponse.ok(promotionService.activeBanners(position));
    }
}
