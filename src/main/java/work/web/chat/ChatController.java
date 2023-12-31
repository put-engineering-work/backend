package work.web.chat;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import work.dto.chat.MessageDTO;

import java.util.List;
import java.util.UUID;

public interface ChatController {
    @MessageMapping("/send/{eventId}")
    @SendTo("/topic/messages/{eventId}")
    MessageDTO sendMessage(ServerHttpRequest request, @DestinationVariable UUID eventId, MessageDTO messageDTO);

    @MessageMapping("/history/{eventId}")
    @SendTo("/topic/history/{eventId}")
    List<MessageDTO> getHistory(ServerHttpRequest request, @DestinationVariable UUID eventId);
}
