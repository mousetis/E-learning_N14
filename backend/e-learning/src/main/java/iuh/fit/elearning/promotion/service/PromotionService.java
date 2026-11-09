package iuh.fit.elearning.promotion.service;

import iuh.fit.elearning.common.exception.BusinessException;
import iuh.fit.elearning.common.exception.ErrorCode;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.common.util.PageUtils;
import iuh.fit.elearning.notification.entity.enums.NotificationType;
import iuh.fit.elearning.notification.service.NotificationService;
import iuh.fit.elearning.promotion.dto.request.BannerRequest;
import iuh.fit.elearning.promotion.dto.request.CouponRequest;
import iuh.fit.elearning.promotion.dto.response.BannerResponse;
import iuh.fit.elearning.promotion.dto.response.CouponResponse;
import iuh.fit.elearning.promotion.entity.Banner;
import iuh.fit.elearning.promotion.entity.Coupon;
import iuh.fit.elearning.promotion.entity.enums.BannerLinkType;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;
import iuh.fit.elearning.promotion.entity.enums.DiscountType;
import iuh.fit.elearning.promotion.mapper.PromotionMapper;
import iuh.fit.elearning.promotion.repository.BannerRepository;
import iuh.fit.elearning.promotion.repository.CouponRepository;
import iuh.fit.elearning.storage.service.StorageService;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import iuh.fit.elearning.user.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** UC024: mã giảm giá và banner; đồng thời là nơi tính giảm giá cho UC010 (SDS 7.8.1). */
@Service
public class PromotionService {

    private final CouponRepository couponRepository;
    private final BannerRepository bannerRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final StorageService storageService;
    private final PromotionMapper mapper;

    public PromotionService(CouponRepository couponRepository, BannerRepository bannerRepository,
                            UserRepository userRepository, NotificationService notificationService,
                            StorageService storageService, PromotionMapper mapper) {
        this.couponRepository = couponRepository;
        this.bannerRepository = bannerRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.storageService = storageService;
        this.mapper = mapper;
    }

    // ------------------------------------------------------------------ tính giảm giá (dùng cho đơn hàng)

    /** Mã giảm giá và số tiền giảm đã tính. */
    public record CouponApplication(Coupon coupon, BigDecimal discount) {
    }

    /**
     * Kiểm tra mã theo SDS 7.8.1: is_active, start_at ≤ now ≤ end_at, subtotal ≥ min_order_value,
     * used_count < usage_limit. Ném COUPON_INVALID kèm lý do.
     */
    @Transactional(readOnly = true)
    public CouponApplication evaluate(String code, BigDecimal subtotal) {
        if (code == null || code.isBlank()) {
            throw new BusinessException(ErrorCode.COUPON_INVALID, "Vui lòng nhập mã giảm giá");
        }
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_INVALID, "Mã giảm giá không tồn tại"));
        Instant now = Instant.now();
        if (!Boolean.TRUE.equals(coupon.getActive())) {
            throw new BusinessException(ErrorCode.COUPON_INVALID, "Mã giảm giá đã bị hủy");
        }
        if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
            throw new BusinessException(ErrorCode.COUPON_INVALID, "Mã giảm giá chưa đến thời gian áp dụng");
        }
        if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
            throw new BusinessException(ErrorCode.COUPON_INVALID, "Mã giảm giá đã hết hạn");
        }
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            throw new BusinessException(ErrorCode.COUPON_INVALID, "Mã giảm giá đã hết lượt sử dụng");
        }
        if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new BusinessException(ErrorCode.COUPON_INVALID,
                    "Đơn hàng chưa đạt giá trị tối thiểu " + coupon.getMinOrderValue().toBigInteger() + "đ để dùng mã");
        }
        return new CouponApplication(coupon, discountOf(coupon, subtotal));
    }

    /** PERCENTAGE: min(subtotal × value / 100, max_discount_amount); FIXED_AMOUNT: min(value, subtotal). */
    public static BigDecimal discountOf(Coupon coupon, BigDecimal subtotal) {
        BigDecimal discount;
        if (coupon.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            if (coupon.getMaxDiscountAmount() != null && coupon.getMaxDiscountAmount().signum() > 0) {
                discount = discount.min(coupon.getMaxDiscountAmount());
            }
        } else {
            discount = coupon.getDiscountValue();
        }
        return discount.min(subtotal).max(BigDecimal.ZERO).setScale(0, RoundingMode.HALF_UP);
    }

    // ------------------------------------------------------------------ banner (public)

    @Transactional(readOnly = true)
    public List<BannerResponse> activeBanners(BannerPosition position) {
        Instant now = Instant.now();
        return bannerRepository.findByPositionAndActiveTrueOrderBySortOrderAsc(position).stream()
                .filter(b -> (b.getStartAt() == null || !now.isBefore(b.getStartAt()))
                        && (b.getEndAt() == null || !now.isAfter(b.getEndAt())))
                .map(mapper::toResponse)
                .toList();
    }

    // ------------------------------------------------------------------ admin: mã giảm giá

    @Transactional(readOnly = true)
    public PageResponse<CouponResponse> listCoupons(int page, int size) {
        return PageResponse.of(couponRepository.findAll(
                PageUtils.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))), mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CouponResponse getCoupon(UUID id) {
        return mapper.toResponse(findCoupon(id));
    }

    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        validateCoupon(request);
        String code = request.code().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(code)) {
            throw new BusinessException(ErrorCode.DUPLICATE, "Mã giảm giá đã tồn tại");
        }
        Coupon coupon = Coupon.builder().code(code).build();
        apply(coupon, request);
        coupon.setActive(true);
        couponRepository.saveAndFlush(coupon);

        // UC024: học viên nhận thông báo khi có khuyến mãi mới
        List<UUID> students = userRepository.findIdsByRoleAndStatus(UserRole.STUDENT, UserStatus.ACTIVE);
        notificationService.notifyUsers(students, NotificationType.PROMOTION, "Khuyến mãi mới",
                "Dùng mã " + code + (request.description() == null ? "" : " - " + request.description()),
                coupon.getId());
        return mapper.toResponse(coupon);
    }

    /** Không đổi mã; mã đã dùng chỉ cho phép điều chỉnh các thông số còn lại. */
    @Transactional
    public CouponResponse updateCoupon(UUID id, CouponRequest request) {
        validateCoupon(request);
        Coupon coupon = findCoupon(id);
        if (!coupon.getCode().equalsIgnoreCase(request.code().trim())) {
            throw BusinessException.badRequest("Không được đổi mã giảm giá, hãy tạo mã mới");
        }
        if (request.usageLimit() != null && request.usageLimit() < coupon.getUsedCount()) {
            throw BusinessException.badRequest("Số lượt tối đa không được nhỏ hơn số lượt đã dùng");
        }
        apply(coupon, request);
        return mapper.toResponse(coupon);
    }

    @Transactional
    public CouponResponse deactivateCoupon(UUID id) {
        Coupon coupon = findCoupon(id);
        coupon.setActive(false);
        return mapper.toResponse(coupon);
    }

    // ------------------------------------------------------------------ admin: banner

    @Transactional(readOnly = true)
    public List<BannerResponse> listBanners(BannerPosition position) {
        List<Banner> banners = position == null
                ? bannerRepository.findAll(Sort.by("position", "sortOrder"))
                : bannerRepository.findByPositionAndActiveTrueOrderBySortOrderAsc(position);
        return banners.stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public BannerResponse createBanner(BannerRequest request) {
        Banner banner = Banner.builder().build();
        apply(banner, request);
        return mapper.toResponse(bannerRepository.save(banner));
    }

    @Transactional
    public BannerResponse updateBanner(UUID id, BannerRequest request) {
        Banner banner = findBanner(id);
        apply(banner, request);
        return mapper.toResponse(banner);
    }

    @Transactional
    public void deleteBanner(UUID id) {
        bannerRepository.delete(findBanner(id));
    }

    // ------------------------------------------------------------------ nội bộ

    private void validateCoupon(CouponRequest r) {
        if (!r.endAt().isAfter(r.startAt())) {
            throw BusinessException.badRequest("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        if (r.discountType() == DiscountType.PERCENTAGE && r.discountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw BusinessException.badRequest("Giảm theo phần trăm tối đa 100%");
        }
    }

    private void apply(Coupon coupon, CouponRequest r) {
        coupon.setDescription(r.description() == null || r.description().isBlank() ? null : r.description().trim());
        coupon.setDiscountType(r.discountType());
        coupon.setDiscountValue(r.discountValue());
        coupon.setMinOrderValue(r.minOrderValue() == null ? BigDecimal.ZERO : r.minOrderValue());
        coupon.setMaxDiscountAmount(r.discountType() == DiscountType.PERCENTAGE ? r.maxDiscountAmount() : null);
        coupon.setUsageLimit(r.usageLimit());
        coupon.setStartAt(r.startAt());
        coupon.setEndAt(r.endAt());
    }

    private void apply(Banner banner, BannerRequest r) {
        BannerLinkType linkType = r.linkType() == null ? BannerLinkType.NONE : r.linkType();
        String linkValue = r.linkValue() == null || r.linkValue().isBlank() ? null : r.linkValue().trim();
        if (linkType != BannerLinkType.NONE && linkValue == null) {
            throw BusinessException.badRequest("Cần nhập giá trị liên kết cho loại liên kết đã chọn");
        }
        if (linkType == BannerLinkType.URL && !(linkValue.startsWith("http://") || linkValue.startsWith("https://"))) {
            throw BusinessException.badRequest("Liên kết URL phải bắt đầu bằng http:// hoặc https://");
        }
        if (r.startAt() != null && r.endAt() != null && !r.endAt().isAfter(r.startAt())) {
            throw BusinessException.badRequest("Thời gian kết thúc phải sau thời gian bắt đầu");
        }
        banner.setTitle(r.title().trim());
        banner.setSubtitle(r.subtitle() == null || r.subtitle().isBlank() ? null : r.subtitle().trim());
        banner.setImageUrl(storageService.requireImageRef(r.imageUrl()));
        banner.setActionLabel(r.actionLabel() == null || r.actionLabel().isBlank() ? null : r.actionLabel().trim());
        banner.setLinkType(linkType);
        banner.setLinkValue(linkType == BannerLinkType.NONE ? null : linkValue);
        banner.setPosition(r.position());
        banner.setSortOrder(r.sortOrder() == null ? 0 : r.sortOrder());
        banner.setStartAt(r.startAt());
        banner.setEndAt(r.endAt());
        if (r.active() != null) {
            banner.setActive(r.active());
        }
    }

    private Coupon findCoupon(UUID id) {
        return couponRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy mã giảm giá"));
    }

    private Banner findBanner(UUID id) {
        return bannerRepository.findById(id).orElseThrow(() -> BusinessException.notFound("Không tìm thấy banner"));
    }
}
