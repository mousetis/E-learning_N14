package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.SavedCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedCourseRepository extends JpaRepository<SavedCourse, UUID> {

    Optional<SavedCourse> findByUserIdAndCourseId(UUID userId, UUID courseId);

    boolean existsByUserIdAndCourseId(UUID userId, UUID courseId);

    Page<SavedCourse> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    long countByUserId(UUID userId);

    @Query("select s.course.id from SavedCourse s where s.user.id = :userId and s.course.id in :courseIds")
    List<UUID> findSavedCourseIds(@Param("userId") UUID userId, @Param("courseIds") Collection<UUID> courseIds);
}
