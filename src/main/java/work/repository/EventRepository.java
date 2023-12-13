package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import work.domain.Event;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
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

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
            "AND e.start_date >=:startDate " +
            "AND e.start_date <= CURRENT_DATE + INTERVAL '1 DAY' - INTERVAL '1 SECOND'",
            nativeQuery = true)
    List<Event> findEventsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("startDate") ZonedDateTime startDate);

    @Query(value = "select e.* from events e join members m on m.event_id=e.id " +
            "where e.id=:eventId and m.user_id=:userId",
            nativeQuery = true)
    Optional<Event> findEventByIdAndUserId(@Param("userId") UUID userId, @Param("eventId") UUID eventId);
}
