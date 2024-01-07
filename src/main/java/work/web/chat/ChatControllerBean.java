package work.web.chat;

import org.springframework.stereotype.Controller;
import work.dto.chat.MessageDTO;
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
    public MessageDTO sendMessage(Principal Principal, UUID eventId, MessageDTO messageDTO) {
        return chatService.sendMessage(Principal, eventId, messageDTO);
    }

    @Override
    public List<MessageDTO> getHistory(Principal principal, UUID eventId) {
        return chatService.getHistory(principal,eventId);
    }
}
