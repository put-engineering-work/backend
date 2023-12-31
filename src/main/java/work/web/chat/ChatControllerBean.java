package work.web.chat;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import work.dto.chat.MessageDTO;
import work.service.chat.ChatService;

import java.util.List;
import java.util.UUID;

@Controller
public class ChatControllerBean implements ChatController {
    private final ChatService chatService;

    public ChatControllerBean(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    public MessageDTO sendMessage(ServerHttpRequest request, UUID eventId, MessageDTO messageDTO) {
        return chatService.sendMessage(request, eventId, messageDTO);
    }

    @Override
    public List<MessageDTO> getHistory(ServerHttpRequest request, UUID eventId) {
        return chatService.getHistory(request,eventId);
    }
}
