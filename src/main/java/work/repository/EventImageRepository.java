package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.domain.EventImage;

import java.util.UUID;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, UUID> {
}
