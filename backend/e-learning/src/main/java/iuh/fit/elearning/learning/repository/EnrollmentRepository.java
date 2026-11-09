package iuh.fit.elearning.learning.repository;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import iuh.fit.elearning.learning.entity.Enrollment;
import iuh.fit.elearning.learning.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
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

    long countByUserIdAndStatus(UUID userId, EnrollmentStatus status);

    long countByCourseId(UUID courseId);

    long countByEnrolledAtBetween(Instant from, Instant to);

    long countByEnrolledAtBetweenAndStatus(Instant from, Instant to, EnrollmentStatus status);

    /** Id các khóa học (trong danh sách) mà người dùng đang sở hữu (ghi danh không ở trạng thái excluded). */
    @Query("select e.course.id from Enrollment e where e.user.id = :userId and e.course.id in :courseIds "
            + "and e.status <> :excluded")
    List<UUID> findOwnedCourseIds(@Param("userId") UUID userId,
                                  @Param("courseIds") Collection<UUID> courseIds,
                                  @Param("excluded") EnrollmentStatus excluded);

    List<Enrollment> findByEnrolledAtBetween(Instant from, Instant to);

    /** Khóa dòng ghi danh khi xử lý hoàn tiền để không hoàn hai lần. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Enrollment e where e.id = :id")
    Optional<Enrollment> findByIdForUpdate(@Param("id") UUID id);
}
