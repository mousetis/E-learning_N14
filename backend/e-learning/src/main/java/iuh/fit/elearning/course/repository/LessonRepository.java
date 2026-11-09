package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findBySectionIdOrderByOrderIndexAsc(UUID sectionId);

    long countBySectionCourseId(UUID courseId);

    @Query("select coalesce(sum(l.durationSeconds), 0) from Lesson l where l.section.course.id = :courseId")
    long sumDurationByCourseId(@Param("courseId") UUID courseId);

    /** Số bài học đã có video - điều kiện gửi duyệt / duyệt khóa học. */
    @Query("select count(l) from Lesson l where l.section.course.id = :courseId "
            + "and l.videoUrl is not null and l.videoUrl <> ''")
    long countWithVideoByCourseId(@Param("courseId") UUID courseId);

    @Query("select coalesce(max(l.orderIndex), 0) from Lesson l where l.section.id = :sectionId")
    int maxOrderIndex(@Param("sectionId") UUID sectionId);

    /** Toàn bộ bài học của khóa học theo thứ tự chương rồi bài. */
    @Query("select l from Lesson l where l.section.course.id = :courseId "
            + "order by l.section.orderIndex asc, l.orderIndex asc")
    List<Lesson> findByCourseIdOrdered(@Param("courseId") UUID courseId);
}
