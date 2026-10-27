package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.CourseBenefit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseBenefitRepository extends JpaRepository<CourseBenefit, UUID> {

    List<CourseBenefit> findByCourseIdOrderBySortOrderAsc(UUID courseId);
}
