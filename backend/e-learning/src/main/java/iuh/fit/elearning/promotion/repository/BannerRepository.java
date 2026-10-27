package iuh.fit.elearning.promotion.repository;

import iuh.fit.elearning.promotion.entity.Banner;
import iuh.fit.elearning.promotion.entity.enums.BannerPosition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BannerRepository extends JpaRepository<Banner, UUID> {

    List<Banner> findByPositionAndActiveTrueOrderBySortOrderAsc(BannerPosition position);
}
