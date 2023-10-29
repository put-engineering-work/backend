package work.util.mapstruct;

import org.mapstruct.Mapper;
import work.user.domain.Event;
import work.user.dto.event.RequestEventDTO;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event requestEventDtoToEvent(RequestEventDTO requestEventDto);
}

