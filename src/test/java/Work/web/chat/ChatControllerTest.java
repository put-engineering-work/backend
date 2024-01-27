package Work.web.chat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import work.dto.chat.MessageDTO;
import work.dto.chat.MessageGetDTO;
import work.dto.chat.Sender;
import work.service.chat.ChatService;
import work.web.chat.ChatControllerBean;

import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class ChatControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private Principal principal;

    @InjectMocks
    private ChatControllerBean chatController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendMessage() {
        UUID eventId = UUID.randomUUID();
        MessageGetDTO messageGetDTO = new MessageGetDTO("Test message");

        UUID messageId = UUID.randomUUID();
        String messageContent = "Hello, World!";
        ZonedDateTime creationDate = ZonedDateTime.now();
        Sender sender = new Sender(UUID.randomUUID(), "John", "Doe");

        MessageDTO messageDTO = new MessageDTO(messageId, messageContent, creationDate, sender);

        when(principal.getName()).thenReturn("user");
        when(chatService.sendMessage(principal, eventId, messageGetDTO)).thenReturn(messageDTO);

        MessageDTO result = chatController.sendMessage(principal, eventId, messageGetDTO);

        verify(chatService).sendMessage(principal, eventId, messageGetDTO);
        verify(principal).getName();
        assert result.equals(messageDTO);
    }

    @Test
    public void testGetHistory() {
        UUID eventId = UUID.randomUUID();

        UUID messageId = UUID.randomUUID();
        String messageContent = "Past message";
        ZonedDateTime creationDate = ZonedDateTime.now();
        Sender sender = new Sender(UUID.randomUUID(), "Jane", "Doe");

        MessageDTO messageDTO = new MessageDTO(messageId, messageContent, creationDate, sender);
        List<MessageDTO> messageDTOList = Collections.singletonList(messageDTO);

        when(principal.getName()).thenReturn("user");
        when(chatService.getHistory(principal, eventId)).thenReturn(messageDTOList);

        List<MessageDTO> result = chatController.getHistory(principal, eventId);

        verify(chatService).getHistory(principal, eventId);
        verify(principal).getName();
        assert result.equals(messageDTOList);
    }
}
