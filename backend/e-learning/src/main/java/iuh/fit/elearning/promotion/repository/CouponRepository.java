package iuh.fit.elearning.promotion.repository;

import iuh.fit.elearning.promotion.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    Optional<Coupon> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    /** Tăng số lượt dùng nếu còn lượt (nguyên tử). Trả về 0 khi đã hết lượt. */
    @Modifying
    @Query("update Coupon c set c.usedCount = c.usedCount + 1 where c.id = :id "
            + "and (c.usageLimit is null or c.usedCount < c.usageLimit)")
    int tryIncrementUsage(@Param("id") UUID id);
}
