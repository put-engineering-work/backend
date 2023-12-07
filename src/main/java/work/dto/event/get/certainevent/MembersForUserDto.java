package work.dto.event.get.certainevent;

import work.domain.AppMemberType;

import java.util.UUID;

public record MembersForUserDto(
        UUID id,

        String name,

        String lastName,

        AppMemberType type
) {
}
