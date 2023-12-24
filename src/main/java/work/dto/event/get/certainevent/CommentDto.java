package work.dto.event.get.certainevent;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CommentDto(
        UUID id,
        String content,
        Integer grade,
        ZonedDateTime commentDate

) {
}
