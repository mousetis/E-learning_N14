package iuh.fit.elearning.cart.repository;

import iuh.fit.elearning.cart.entity.CartItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    boolean existsByCartIdAndCourseId(UUID cartId, UUID courseId);

    Optional<CartItem> findByCartIdAndCourseId(UUID cartId, UUID courseId);
}
