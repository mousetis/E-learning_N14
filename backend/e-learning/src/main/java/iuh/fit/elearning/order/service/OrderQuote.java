package iuh.fit.elearning.order.service;

import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.promotion.entity.Coupon;

import java.math.BigDecimal;
import java.util.List;

/** Kết quả tính tiền đơn hàng (SDS 7.8.1). coupon null nếu không áp mã. */
public record OrderQuote(
        List<Course> courses,
        BigDecimal subtotal,
        Coupon coupon,
        BigDecimal discount,
        BigDecimal total
) {
}
