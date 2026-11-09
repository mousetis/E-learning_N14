package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.Course;
import iuh.fit.elearning.course.entity.enums.CourseStatus;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    Optional<Course> findBySlug(String slug);

    Page<Course> findByStatus(CourseStatus status, Pageable pageable);

    Page<Course> findByCategoryIdAndStatus(UUID categoryId, CourseStatus status, Pageable pageable);

    List<Course> findByInstructorIdOrderByCreatedAtDesc(UUID instructorId);

    boolean existsByCategoryId(UUID categoryId);

    @Query("select count(c) > 0 from Course c join c.topics t where t.id = :topicId")
    boolean existsByTopicId(@Param("topicId") UUID topicId);

    long countByCategoryIdAndStatus(UUID categoryId, CourseStatus status);

    long countByInstructorIdAndStatus(UUID instructorId, CourseStatus status);

    boolean existsByInstructorIdAndStatus(UUID instructorId, CourseStatus status);

    /** Số học viên của các khóa PUBLISHED cùng danh mục, giảm dần - dùng tính ngưỡng Best-seller (top 10%). */
    @Query("select c.studentCount from Course c where c.category.id = :categoryId and c.status = :status "
            + "order by c.studentCount desc")
    List<Integer> findTopStudentCounts(@Param("categoryId") UUID categoryId,
                                       @Param("status") CourseStatus status,
                                       Pageable pageable);

    /** Khóa học tương tự: cùng danh mục, đang mở bán, giảng viên còn hoạt động. */
    @Query("select c from Course c where c.category.id = :categoryId and c.id <> :courseId "
            + "and c.status = :status and c.instructor.status = :instructorStatus "
            + "order by c.studentCount desc, c.avgRating desc")
    List<Course> findSimilar(@Param("categoryId") UUID categoryId,
                             @Param("courseId") UUID courseId,
                             @Param("status") CourseStatus status,
                             @Param("instructorStatus") UserStatus instructorStatus,
                             Pageable pageable);

    @Query("select c from Course c where c.instructor.id = :instructorId and c.status = :status "
            + "order by c.studentCount desc, c.avgRating desc")
    List<Course> findByInstructorAndStatus(@Param("instructorId") UUID instructorId,
                                           @Param("status") CourseStatus status);

    @Query("select c from Course c where c.status = :status and c.instructor.status = :instructorStatus "
            + "order by c.studentCount asc, c.createdAt desc")
    List<Course> findLeastEnrolled(@Param("status") CourseStatus status,
                                   @Param("instructorStatus") UserStatus instructorStatus,
                                   Pageable pageable);

    @Query("select c from Course c where c.status = :status and c.reviewCount > 0 "
            + "order by c.avgRating desc, c.reviewCount desc")
    List<Course> findTopRated(@Param("status") CourseStatus status, Pageable pageable);

    @Query("select c from Course c where c.status = :status order by c.studentCount desc")
    List<Course> findBestSelling(@Param("status") CourseStatus status, Pageable pageable);

    @Modifying
    @Query("update Course c set c.studentCount = c.studentCount + :delta where c.id = :id")
    int addStudentCount(@Param("id") UUID id, @Param("delta") int delta);

    @Modifying
    @Query("update Course c set c.likeCount = c.likeCount + :delta where c.id = :id")
    int addLikeCount(@Param("id") UUID id, @Param("delta") int delta);

    @Modifying(flushAutomatically = true)
    @Query("update Course c set c.avgRating = :avg, c.reviewCount = :count where c.id = :id")
    int updateRating(@Param("id") UUID id, @Param("avg") BigDecimal avg, @Param("count") int count);
}
