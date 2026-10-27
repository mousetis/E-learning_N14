package iuh.fit.elearning.promotion.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Lượt sử dụng mã giảm giá (mỗi đơn hàng tối đa một mã).
 */
@Entity
@Table(name = "coupon_usages")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CouponUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "discount_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountAmount;
}
