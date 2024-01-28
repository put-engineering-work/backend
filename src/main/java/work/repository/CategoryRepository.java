package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import work.domain.EventCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<EventCategory, UUID> {
    Optional<EventCategory> findByName(String name);

    @Query(value = "select * from event_categories where name like %:name%", nativeQuery = true)
    List<EventCategory> findByNameContains(@Param("name") String name);
}
