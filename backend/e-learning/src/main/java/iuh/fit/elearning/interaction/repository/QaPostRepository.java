package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.QaPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QaPostRepository extends JpaRepository<QaPost, UUID> {

    Page<QaPost> findByCourseIdAndHiddenFalseAndDeletedAtIsNullOrderByCreatedAtDesc(UUID courseId, Pageable pageable);

    Optional<QaPost> findByIdAndDeletedAtIsNull(UUID id);

    /** Danh sách cho giảng viên: câu hỏi chưa trả lời lên đầu, gồm cả câu hỏi đang bị ẩn. */
    @Query("select p from QaPost p where p.course.id = :courseId and p.deletedAt is null "
            + "order by p.answered asc, p.createdAt desc")
    Page<QaPost> findForInstructor(@Param("courseId") UUID courseId, Pageable pageable);

    @Modifying
    @Query("update QaPost p set p.commentCount = p.commentCount + :delta where p.id = :id")
    int addCommentCount(@Param("id") UUID id, @Param("delta") int delta);

    @Modifying
    @Query("update QaPost p set p.likeCount = p.likeCount + :delta where p.id = :id")
    int addLikeCount(@Param("id") UUID id, @Param("delta") int delta);
}
