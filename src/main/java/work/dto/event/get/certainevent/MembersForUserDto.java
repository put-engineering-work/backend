package work.dto.event.get.certainevent;

import java.util.UUID;

public record MembersForUserDto(
        UUID id,

        String name,

        String lastName
) {
}
