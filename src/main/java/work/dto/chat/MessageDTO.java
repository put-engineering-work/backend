package work.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MessageDTO {
    private UUID id;
    private String message;
    private ZonedDateTime createdDate;
    private Sender sender;

    public MessageDTO(String message) {
        this.message = message;
    }
}
