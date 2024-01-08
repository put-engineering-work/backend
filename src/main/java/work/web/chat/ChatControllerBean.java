package work.web.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import work.dto.chat.MessageDTO;
import work.dto.chat.MessageGetDTO;
import work.service.chat.ChatService;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class ChatControllerBean implements ChatController {
    private final ChatService chatService;

    public ChatControllerBean(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    @MessageMapping("/send/{eventId}")
    @SendTo("/topic/messages/{eventId}")
    public MessageDTO sendMessage(
            Principal principal,
            @DestinationVariable("eventId") UUID eventId,
            @Payload MessageGetDTO messageDTO
    ) {
        log.info(principal.getName());
        var response=chatService.sendMessage(principal, eventId, messageDTO);
        log.info(response.toString());
        return response;
    }

    @Override
    @MessageMapping("/history/{eventId}")
    @SendTo("/topic/history/{eventId}")
    public List<MessageDTO> getHistory(
            Principal principal,
            @DestinationVariable("eventId") UUID eventId) {
        var response=chatService.getHistory(principal,eventId);
        log.info(principal.getName());
        log.info(response.toString());
        return response;
    }
}
