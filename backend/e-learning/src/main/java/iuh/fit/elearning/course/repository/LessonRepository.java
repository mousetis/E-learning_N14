package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.Lesson;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    List<Lesson> findBySectionIdOrderByOrderIndexAsc(UUID sectionId);

    long countBySectionCourseId(UUID courseId);
}
