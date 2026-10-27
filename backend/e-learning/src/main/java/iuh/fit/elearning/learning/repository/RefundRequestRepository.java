package iuh.fit.elearning.learning.repository;

import iuh.fit.elearning.learning.entity.RefundRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RefundRequestRepository extends JpaRepository<RefundRequest, UUID> {

    List<RefundRequest> findByEnrollmentId(UUID enrollmentId);
}
