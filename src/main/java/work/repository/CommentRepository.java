package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import work.domain.Comment;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    @Query(value = "select c.* from comments c where c.event_id=:eventId",nativeQuery = true)
    List<Comment> findCommentsByEventId(@Param("eventId") UUID eventId);
}
