package iuh.fit.elearning.cart.controller;

import iuh.fit.elearning.auth.security.AuthUser;
import iuh.fit.elearning.cart.dto.request.AddCartItemRequest;
import iuh.fit.elearning.cart.dto.response.AddToCartResponse;
import iuh.fit.elearning.cart.dto.response.CartResponse;
import iuh.fit.elearning.cart.service.CartService;
import iuh.fit.elearning.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** UC009: giỏ hàng. */
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ApiResponse<CartResponse> cart(@AuthenticationPrincipal AuthUser user) {
        return ApiResponse.ok(cartService.getCart(user.id()));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AddToCartResponse> add(@AuthenticationPrincipal AuthUser user,
                                              @Valid @RequestBody AddCartItemRequest request) {
        AddToCartResponse result = cartService.addItem(user, request.courseId());
        return ApiResponse.ok(result.enrolled() ? "Khóa học miễn phí đã được thêm vào danh sách học của bạn"
                : "Đã thêm vào giỏ hàng", result);
    }

    @DeleteMapping("/items/{courseId}")
    public ApiResponse<CartResponse> remove(@AuthenticationPrincipal AuthUser user, @PathVariable UUID courseId) {
        return ApiResponse.ok("Đã xóa khỏi giỏ hàng", cartService.removeItem(user.id(), courseId));
    }
}
