package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.CourseLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseLikeRepository extends JpaRepository<CourseLike, UUID> {

    Optional<CourseLike> findByCourseIdAndUserId(UUID courseId, UUID userId);
}
