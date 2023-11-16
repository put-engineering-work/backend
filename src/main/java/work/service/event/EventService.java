package work.service.event;

import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;

import java.util.List;

public interface EventService {
    ResponseObject createEvent(EventCreateDto eventToCreate);

    List<EventsInRadiusDto> getEventsWithinRadius(double latitude, double longitude, double radius);
}
