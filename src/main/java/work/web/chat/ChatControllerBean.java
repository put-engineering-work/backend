package work.web.chat;

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
public class ChatControllerBean implements ChatController {
    private final ChatService chatService;

    public ChatControllerBean(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    @MessageMapping("/send/{eventId}")
    @SendTo("/topic/messages/{eventId}")
    public MessageDTO sendMessage(Principal Principal, @DestinationVariable("eventId") UUID eventId
            ,
                           @Payload MessageGetDTO messageDTO
    ) {
//        var messageDTO=new MessageGetDTO("");
        return chatService.sendMessage(Principal, eventId, messageDTO);
    }

    @Override
    @MessageMapping("/history/{eventId}")
    @SendTo("/topic/history/{eventId}")
    public List<MessageDTO> getHistory(Principal principal, @DestinationVariable("eventId") UUID eventId) {
        return chatService.getHistory(principal,eventId);
    }
}
