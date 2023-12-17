package work.dto.event.get.certainevent;

import org.hibernate.annotations.GenericGenerator;
import work.domain.Event;
import work.domain.User;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        String content,
        Integer grade,
        ZonedDateTime commentDate

) {
}
