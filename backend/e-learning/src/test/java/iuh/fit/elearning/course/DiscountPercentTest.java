package iuh.fit.elearning.course;

import iuh.fit.elearning.course.mapper.CourseMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountPercentTest {

    @Test
    void computesRoundedPercent() {
        assertEquals(30, CourseMapper.discountPercent(new BigDecimal("1000000"), new BigDecimal("700000")));
        assertEquals(33, CourseMapper.discountPercent(new BigDecimal("300000"), new BigDecimal("200000")));
    }

    @Test
    void noDiscountCases() {
        assertEquals(0, CourseMapper.discountPercent(null, new BigDecimal("1")));
        assertEquals(0, CourseMapper.discountPercent(new BigDecimal("100"), new BigDecimal("100")));
        assertEquals(0, CourseMapper.discountPercent(BigDecimal.ZERO, BigDecimal.ZERO));
    }
}
