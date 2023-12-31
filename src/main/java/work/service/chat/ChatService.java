package work.service.chat;

import org.springframework.http.server.ServerHttpRequest;
import work.dto.chat.MessageDTO;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    MessageDTO sendMessage(ServerHttpRequest request, UUID eventId, MessageDTO messageDTO);

    List<MessageDTO> getHistory(ServerHttpRequest request, UUID eventId);
}
