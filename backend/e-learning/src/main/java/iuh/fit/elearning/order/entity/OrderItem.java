package iuh.fit.elearning.order.entity;

import iuh.fit.elearning.common.entity.BaseEntity;
import iuh.fit.elearning.course.entity.Course;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * Chi tiết đơn hàng: lưu snapshot tên và giá khóa học lúc mua (ChiTietDonHang).
 */
@Entity
@Table(name = "order_items",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id", "course_id"})
        })
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class OrderItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
}
