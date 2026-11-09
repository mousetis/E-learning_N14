package iuh.fit.elearning.learning;

import iuh.fit.elearning.learning.service.RefundService;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.OrderItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RefundAmountTest {

    private static OrderItem item(String subtotal, String discount, String price) {
        Order order = Order.builder().subtotal(new BigDecimal(subtotal)).discountAmount(new BigDecimal(discount)).build();
        return OrderItem.builder().order(order).price(new BigDecimal(price)).build();
    }

    @Test
    void noDiscountRefundsFullPrice() {
        assertEquals(0, new BigDecimal("500000").compareTo(RefundService.refundAmount(item("1000000", "0", "500000"))));
    }

    @Test
    void discountIsSharedProportionallyToPrice() {
        // đơn 1.000.000, giảm 100.000; khóa 250.000 chịu 25.000 -> hoàn 225.000
        assertEquals(0, new BigDecimal("225000").compareTo(RefundService.refundAmount(item("1000000", "100000", "250000"))));
    }

    @Test
    void freeOrderRefundsNothing() {
        assertEquals(0, BigDecimal.ZERO.compareTo(RefundService.refundAmount(item("0", "0", "0"))));
    }
}
