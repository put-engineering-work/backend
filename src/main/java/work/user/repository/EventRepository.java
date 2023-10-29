package work.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import work.user.domain.Event;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {

    Optional<Event> findByTitle(String title);
    Optional<Event> findByCode(String code);

    Optional<Event> findById(Integer id);
}
