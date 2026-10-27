package iuh.fit.elearning.user.repository;

import iuh.fit.elearning.user.entity.InstructorProfile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, UUID> {

    Optional<InstructorProfile> findByUserId(UUID userId);
}
