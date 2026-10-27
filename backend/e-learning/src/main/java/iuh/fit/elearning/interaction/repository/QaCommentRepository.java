package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.QaComment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QaCommentRepository extends JpaRepository<QaComment, UUID> {

    List<QaComment> findByPostIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID postId);
}
