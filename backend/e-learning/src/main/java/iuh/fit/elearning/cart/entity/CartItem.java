package iuh.fit.elearning.cart.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.course.entity.Course;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Mục trong giỏ hàng (MucGioHang).
 */
@Entity
@Table(name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cart_id", "course_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "price_at_added", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceAtAdded;
}
