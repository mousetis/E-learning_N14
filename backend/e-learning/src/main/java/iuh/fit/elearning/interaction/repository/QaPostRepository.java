package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.QaPost;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QaPostRepository extends JpaRepository<QaPost, UUID> {

    Page<QaPost> findByCourseIdAndHiddenFalseAndDeletedAtIsNullOrderByCreatedAtDesc(UUID courseId, Pageable pageable);
}
