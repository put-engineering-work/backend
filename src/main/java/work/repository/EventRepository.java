package work.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
            "and e.end_date>=CURRENT_DATE " +
            "order by e.start_date desc",
            nativeQuery = true)
    List<Event> findEventsWithinRadius(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
            "AND e.start_date >=:startDate " +
            "AND e.start_date <= CURRENT_DATE + INTERVAL '1 DAY' - INTERVAL '1 SECOND' " +
            "order by e.start_date desc",
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


    @Query(value = "select e.* from events e where e.end_date>=:currentDate order by e.start_date DESC LIMIT :number", nativeQuery = true)
    List<Event> findLastNEvents(@Param("number") Integer number, @Param("currentDate") ZonedDateTime currentDate);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
            "AND e.start_date >= :startDate " +
            "AND e.start_date <= CURRENT_DATE + INTERVAL '1 DAY' - INTERVAL '1 SECOND' " +
            "order by e.start_date desc",
            countQuery = "SELECT count(*) FROM events e " +
                    "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
                    "AND e.start_date >= :startDate " +
                    "AND e.start_date <= CURRENT_DATE + INTERVAL '1 DAY' - INTERVAL '1 SECOND'",
            nativeQuery = true)
    Page<Event> findEventsWithinRadiusWithPagination(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("startDate") ZonedDateTime startDate,
            Pageable pageable);

    @Query(value = "SELECT e.* FROM events e " +
            "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
            "and e.end_date >= CURRENT_DATE " +
            "order by e.start_date desc",
            countQuery = "SELECT count(*) FROM events e " +
                    "WHERE st_distancesphere(e.location, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)) <= :radius " +
                    "and e.end_date >= CURRENT_DATE",
            nativeQuery = true)
    Page<Event> findEventsWithinRadiusWithPagination(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            Pageable pageable);

    @Query(value = "SELECT e.* " +
            "FROM events e " +
            "JOIN members m ON e.id = m.event_id " +
            "WHERE m.user_id =:userId " +
            "ORDER BY e.start_date DESC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Event> findLastNEventsForUser(@Param("userId") UUID userId, @Param("limit") Integer limit);

    @Query("SELECT e FROM Event e JOIN e.categories c WHERE c.name IN :categories AND e NOT IN (SELECT m.event FROM Member m WHERE m.user.id = :userId)")
    List<Event> findRecommendedEvents(@Param("categories") List<String> categories, @Param("userId") UUID userId);

}
