package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.QaPostLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QaPostLikeRepository extends JpaRepository<QaPostLike, UUID> {

    Optional<QaPostLike> findByPostIdAndUserId(UUID postId, UUID userId);

    @Query("select l.post.id from QaPostLike l where l.user.id = :userId and l.post.id in :postIds")
    List<UUID> findLikedPostIds(@Param("userId") UUID userId, @Param("postIds") Collection<UUID> postIds);
}
