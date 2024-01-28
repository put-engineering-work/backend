package work.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Sender {
    private UUID senderId;
    private String name;
    private String lastname;
}
