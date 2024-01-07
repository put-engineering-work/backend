package work.web.chat;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import work.dto.chat.MessageDTO;
import work.dto.chat.MessageGetDTO;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ChatController {
    @MessageMapping("/send/{eventId}")
    @SendTo("/topic/messages/{eventId}")
    MessageDTO sendMessage(Principal Principal, @DestinationVariable("eventId") UUID eventId, @Payload MessageGetDTO messageDTO);

    @MessageMapping("/history/{eventId}")
    @SendTo("/topic/history/{eventId}")
    List<MessageDTO> getHistory(Principal principal, @DestinationVariable("eventId") UUID eventId);
}
