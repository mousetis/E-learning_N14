package iuh.fit.elearning.cart.entity;

import iuh.fit.elearning.common.entity.AuditableEntity;
import iuh.fit.elearning.user.entity.User;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Giỏ hàng của học viên (GioHang).
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Cart extends AuditableEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<CartItem> items = new ArrayList<>();
}
