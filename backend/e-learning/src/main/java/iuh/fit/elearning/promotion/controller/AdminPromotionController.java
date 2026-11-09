package iuh.fit.elearning.promotion.controller;

import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.promotion.dto.request.BannerRequest;
import iuh.fit.elearning.promotion.dto.request.CouponRequest;
import iuh.fit.elearning.promotion.dto.response.BannerResponse;
import iuh.fit.elearning.promotion.dto.response.CouponResponse;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;
import iuh.fit.elearning.promotion.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** UC024: quản lý mã giảm giá, banner (ADMIN). */
@RestController
@RequestMapping("/api/v1/admin")
public class AdminPromotionController {

    private final PromotionService promotionService;

    public AdminPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/coupons")
    public ApiResponse<PageResponse<CouponResponse>> coupons(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(promotionService.listCoupons(page, size));
    }

    @GetMapping("/coupons/{id}")
    public ApiResponse<CouponResponse> coupon(@PathVariable UUID id) {
        return ApiResponse.ok(promotionService.getCoupon(id));
    }

    @PostMapping("/coupons")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok("Đã tạo mã giảm giá", promotionService.createCoupon(request));
    }

    @PutMapping("/coupons/{id}")
    public ApiResponse<CouponResponse> updateCoupon(@PathVariable UUID id, @Valid @RequestBody CouponRequest request) {
        return ApiResponse.ok("Đã cập nhật mã giảm giá", promotionService.updateCoupon(id, request));
    }

    @PatchMapping("/coupons/{id}/deactivate")
    public ApiResponse<CouponResponse> deactivateCoupon(@PathVariable UUID id) {
        return ApiResponse.ok("Đã hủy mã giảm giá", promotionService.deactivateCoupon(id));
    }

    @GetMapping("/banners")
    public ApiResponse<List<BannerResponse>> banners(@RequestParam(required = false) BannerPosition position) {
        return ApiResponse.ok(promotionService.listBanners(position));
    }

    @PostMapping("/banners")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BannerResponse> createBanner(@Valid @RequestBody BannerRequest request) {
        return ApiResponse.ok("Đã tạo banner", promotionService.createBanner(request));
    }

    @PutMapping("/banners/{id}")
    public ApiResponse<BannerResponse> updateBanner(@PathVariable UUID id, @Valid @RequestBody BannerRequest request) {
        return ApiResponse.ok("Đã cập nhật banner", promotionService.updateBanner(id, request));
    }

    @DeleteMapping("/banners/{id}")
    public ApiResponse<Void> deleteBanner(@PathVariable UUID id) {
        promotionService.deleteBanner(id);
        return ApiResponse.message("Đã xóa banner");
    }
}
