package work.service.event;

import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventToCreate);

    List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, double latitude, double longitude, double radius);
}
