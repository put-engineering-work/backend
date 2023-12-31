package work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import work.domain.Message;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
