package iuh.fit.elearning.catalog.repository;

import iuh.fit.elearning.catalog.entity.Topic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {

    List<Topic> findByHotTrueOrderBySearchCountDesc();

    Optional<Topic> findBySlug(String slug);
}
