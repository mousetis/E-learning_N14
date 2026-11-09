package iuh.fit.elearning.user.repository;

import iuh.fit.elearning.user.entity.User;
import iuh.fit.elearning.user.entity.enums.UserRole;
import iuh.fit.elearning.user.entity.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select u.id from User u where u.role = :role and u.status = :status")
    List<UUID> findIdsByRoleAndStatus(@Param("role") UserRole role, @Param("status") UserStatus status);

    long countByRoleAndCreatedAtBetween(UserRole role, Instant from, Instant to);

    List<User> findByRoleAndCreatedAtBetween(UserRole role, Instant from, Instant to);
}
