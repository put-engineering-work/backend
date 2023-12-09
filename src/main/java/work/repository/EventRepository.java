package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import work.domain.Event;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    @Query(value = "SELECT e.* FROM events e " +
            "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius",
            nativeQuery = true)
    List<Event> findEventsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius);
}
