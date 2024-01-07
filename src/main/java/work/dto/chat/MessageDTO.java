package work.dto.chat;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Boolean isOwner=Boolean.FALSE;
    private Sender sender;
}
