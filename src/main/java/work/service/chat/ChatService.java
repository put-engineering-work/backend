package work.service.chat;

import work.dto.chat.MessageDTO;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface ChatService {

    MessageDTO sendMessage(Principal principal, UUID eventId, MessageDTO messageDTO);

    List<MessageDTO> getHistory(Principal principal, UUID eventId);
}
