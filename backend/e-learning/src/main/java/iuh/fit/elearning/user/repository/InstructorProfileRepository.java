package iuh.fit.elearning.user.repository;

import iuh.fit.elearning.user.entity.InstructorProfile;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, UUID> {

    Optional<InstructorProfile> findByUserId(UUID userId);

    Page<InstructorProfile> findByUserStatusAndCourseCountGreaterThan(UserStatus status, int courseCount, Pageable pageable);

    @Modifying
    @Query("update InstructorProfile p set p.studentCount = p.studentCount + :delta where p.user.id = :userId")
    int addStudentCount(@Param("userId") UUID userId, @Param("delta") int delta);

    @Modifying
    @Query("update InstructorProfile p set p.followerCount = p.followerCount + :delta where p.user.id = :userId")
    int addFollowerCount(@Param("userId") UUID userId, @Param("delta") int delta);

    @Modifying(flushAutomatically = true)
    @Query("update InstructorProfile p set p.avgRating = :avg, p.reviewCount = :count where p.user.id = :userId")
    int updateRating(@Param("userId") UUID userId, @Param("avg") BigDecimal avg, @Param("count") int count);
}
