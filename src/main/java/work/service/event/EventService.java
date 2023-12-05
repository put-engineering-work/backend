package work.service.event;

import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.certainevent.CertainEventDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

public interface EventService {
    ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventToCreate);

    ResponseObject addCurrentUserToEvent(HttpServletRequest request, UUID eventId);

    List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, SearchEventDTO searchEventDTO);

    CertainEventDto getCertainEvent(UUID id);
}
