package iuh.fit.elearning.interaction.repository;

import iuh.fit.elearning.interaction.entity.Follow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    Optional<Follow> findByFollowerIdAndInstructorId(UUID followerId, UUID instructorId);

    boolean existsByFollowerIdAndInstructorId(UUID followerId, UUID instructorId);

    List<Follow> findByInstructorId(UUID instructorId);
}
