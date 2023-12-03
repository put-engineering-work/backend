package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import work.domain.Member;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    @Query(value = "select * from members where event_id=:eventId and type='ROLE_HOST'", nativeQuery = true)
    Optional<Member> findEventHost(@Param("eventId") UUID eventId);
}
