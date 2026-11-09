package iuh.fit.elearning.order.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.common.response.ApiResponse;
import iuh.fit.elearning.common.response.PageResponse;
import iuh.fit.elearning.order.dto.request.CreateOrderRequest;
import iuh.fit.elearning.order.dto.response.OrderResponse;
import iuh.fit.elearning.order.service.OrderQuote;
import iuh.fit.elearning.order.service.OrderService;
import iuh.fit.elearning.promotion.dto.request.ValidateCouponRequest;
import iuh.fit.elearning.promotion.dto.response.CouponValidationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** UC010: mã giảm giá, đơn hàng. */
@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/coupons/validate")
    public ApiResponse<CouponValidationResponse> validateCoupon(@AuthenticationPrincipal AuthUser user,
                                                                @Valid @RequestBody ValidateCouponRequest request) {
        OrderQuote quote = orderService.quote(user.id(), request.courseIds(), request.code());
        return ApiResponse.ok(new CouponValidationResponse(quote.coupon().getCode(), quote.coupon().getDescription(),
                quote.subtotal(), quote.discount(), quote.total()));
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> create(@AuthenticationPrincipal AuthUser user,
                                             @Valid @RequestBody CreateOrderRequest request,
                                             HttpServletRequest http) {
        return ApiResponse.ok(orderService.create(user, request, clientIp(http)));
    }

    @GetMapping("/orders/{orderCode}")
    public ApiResponse<OrderResponse> get(@AuthenticationPrincipal AuthUser user, @PathVariable String orderCode) {
        return ApiResponse.ok(orderService.get(user.id(), orderCode));
    }

    @GetMapping("/orders")
    public ApiResponse<PageResponse<OrderResponse>> list(@AuthenticationPrincipal AuthUser user,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.ok(orderService.list(user.id(), page, size));
    }

    private static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
