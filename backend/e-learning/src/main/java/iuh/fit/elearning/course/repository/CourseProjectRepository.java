package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.CourseProject;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseProjectRepository extends JpaRepository<CourseProject, UUID> {

    Optional<CourseProject> findByCourseId(UUID courseId);
}
