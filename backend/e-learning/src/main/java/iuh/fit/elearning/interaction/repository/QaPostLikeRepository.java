package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.QaPostLike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QaPostLikeRepository extends JpaRepository<QaPostLike, UUID> {

    Optional<QaPostLike> findByPostIdAndUserId(UUID postId, UUID userId);
}
