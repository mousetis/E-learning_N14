package iuh.fit.elearning.learning.repository;

import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    Optional<Enrollment> findByUserIdAndCourseId(UUID userId, UUID courseId);

    boolean existsByUserIdAndCourseIdAndStatusNot(UUID userId, UUID courseId, EnrollmentStatus status);

    List<Enrollment> findByUserIdAndStatusNotOrderByLastAccessedAtDesc(UUID userId, EnrollmentStatus status);

    List<Enrollment> findByUserIdAndStatusOrderByLastAccessedAtDesc(UUID userId, EnrollmentStatus status);

    boolean existsByCourseId(UUID courseId);
}
