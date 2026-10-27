package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.CourseDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseDocumentRepository extends JpaRepository<CourseDocument, UUID> {

    List<CourseDocument> findByCourseId(UUID courseId);
}
