package iuh.fit.elearning.course.repository;

import iuh.fit.elearning.course.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    List<Section> findByCourseIdOrderByOrderIndexAsc(UUID courseId);

    long countByCourseId(UUID courseId);

    @Query("select coalesce(max(s.orderIndex), 0) from Section s where s.course.id = :courseId")
    int maxOrderIndex(@Param("courseId") UUID courseId);
}
