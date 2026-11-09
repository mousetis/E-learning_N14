package iuh.fit.elearning.cart.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Giỏ hàng: tạm tính theo giá bán hiện hành, savings = tổng giá gốc - tạm tính. */
public record CartResponse(
        List<CartItemResponse> items,
        int itemCount,
        BigDecimal originalTotal,
        BigDecimal subtotal,
        BigDecimal savings
) {
}
