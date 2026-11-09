package iuh.fit.elearning.cart.repository;

import java.util.Collection;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import iuh.fit.elearning.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    boolean existsByCartIdAndCourseId(UUID cartId, UUID courseId);

    Optional<CartItem> findByCartIdAndCourseId(UUID cartId, UUID courseId);

    boolean existsByCartUserIdAndCourseId(UUID userId, UUID courseId);

    /** Xóa các khóa học đã mua khỏi giỏ hàng của người dùng. */
    @Modifying
    @Query("delete from CartItem i where i.cart.user.id = :userId and i.course.id in :courseIds")
    int deleteByUserAndCourses(@Param("userId") UUID userId, @Param("courseIds") Collection<UUID> courseIds);

    List<CartItem> findByCartIdOrderByCreatedAtAsc(UUID cartId);
}
