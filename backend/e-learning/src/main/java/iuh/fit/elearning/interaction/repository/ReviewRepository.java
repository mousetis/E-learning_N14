package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByCourseIdAndUserId(UUID courseId, UUID userId);

    Page<Review> findByCourseIdOrderByCreatedAtDesc(UUID courseId, Pageable pageable);

    Page<Review> findByCourseIdAndRatingOrderByCreatedAtDesc(UUID courseId, Integer rating, Pageable pageable);
}
