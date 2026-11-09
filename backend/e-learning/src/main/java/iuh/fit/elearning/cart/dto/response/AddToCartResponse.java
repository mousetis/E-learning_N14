package iuh.fit.elearning.cart.dto.response;

/** enrolled = true khi khóa học miễn phí được ghi danh ngay (không thêm vào giỏ). */
public record AddToCartResponse(boolean enrolled, CartResponse cart) {
}
