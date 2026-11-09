package iuh.fit.elearning.order.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import iuh.fit.elearning.order.entity.Order;
import iuh.fit.elearning.order.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderCode(String orderCode);

    Optional<Order> findByOrderCodeAndUserId(String orderCode, UUID userId);

    Page<Order> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    List<Order> findByStatusAndExpiresAtBefore(OrderStatus status, Instant time);

    List<Order> findByStatusAndPaidAtBetween(OrderStatus status, Instant from, Instant to);

    /** Khóa dòng đơn hàng khi xử lý kết quả thanh toán để IPN gửi trùng không xử lý hai lần. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from PurchaseOrder o where o.orderCode = :orderCode")
    Optional<Order> findByOrderCodeForUpdate(@Param("orderCode") String orderCode);
}
