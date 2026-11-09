package iuh.fit.elearning.promotion;

import iuh.fit.elearning.promotion.entity.Coupon;
import iuh.fit.elearning.promotion.entity.enums.DiscountType;
import iuh.fit.elearning.promotion.service.PromotionService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CouponDiscountTest {

    private static Coupon coupon(DiscountType type, String value, String max) {
        return Coupon.builder().code("X").discountType(type).discountValue(new BigDecimal(value))
                .maxDiscountAmount(max == null ? null : new BigDecimal(max)).build();
    }

    private static void assertMoney(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual), "expected " + expected + " but was " + actual);
    }

    @Test
    void percentageIsCappedByMaxDiscount() {
        assertMoney("100000", PromotionService.discountOf(coupon(DiscountType.PERCENTAGE, "20", "100000"), new BigDecimal("1000000")));
    }

    @Test
    void percentageWithoutCap() {
        assertMoney("200000", PromotionService.discountOf(coupon(DiscountType.PERCENTAGE, "20", null), new BigDecimal("1000000")));
    }

    @Test
    void fixedAmountNeverExceedsSubtotal() {
        assertMoney("50000", PromotionService.discountOf(coupon(DiscountType.FIXED_AMOUNT, "80000", null), new BigDecimal("50000")));
        assertMoney("30000", PromotionService.discountOf(coupon(DiscountType.FIXED_AMOUNT, "30000", null), new BigDecimal("50000")));
    }
}
