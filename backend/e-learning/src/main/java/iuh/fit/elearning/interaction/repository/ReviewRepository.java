package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Optional<Review> findByCourseIdAndUserId(UUID courseId, UUID userId);

    Page<Review> findByCourseIdOrderByCreatedAtDesc(UUID courseId, Pageable pageable);

    Page<Review> findByCourseIdAndRatingOrderByCreatedAtDesc(UUID courseId, Integer rating, Pageable pageable);

    Page<Review> findByCourseInstructorIdOrderByCreatedAtDesc(UUID instructorId, Pageable pageable);

    long countByCourseId(UUID courseId);

    long countByCourseInstructorId(UUID instructorId);

    @Query("select avg(r.rating) from Review r where r.course.id = :courseId")
    Double averageRatingByCourse(@Param("courseId") UUID courseId);

    @Query("select avg(r.rating) from Review r where r.course.instructor.id = :instructorId")
    Double averageRatingByInstructor(@Param("instructorId") UUID instructorId);

    /** Mỗi phần tử: [rating (Integer), số lượng (Long)]. */
    @Query("select r.rating, count(r) from Review r where r.course.id = :courseId group by r.rating")
    List<Object[]> ratingDistribution(@Param("courseId") UUID courseId);
}
