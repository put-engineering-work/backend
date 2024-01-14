package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.domain.EventCategory;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<EventCategory, UUID> {
    Optional<EventCategory> findByName(String name);
}
