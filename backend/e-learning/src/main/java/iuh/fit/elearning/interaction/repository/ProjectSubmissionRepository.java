package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.ProjectSubmission;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectSubmissionRepository extends JpaRepository<ProjectSubmission, UUID> {

    Optional<ProjectSubmission> findByCourseIdAndUserId(UUID courseId, UUID userId);

    Page<ProjectSubmission> findByCourseIdOrderByCreatedAtDesc(UUID courseId, Pageable pageable);
}
