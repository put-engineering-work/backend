package work.web.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.service.event.EventService;

import java.util.List;

@RestController
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@CrossOrigin
public class EventControllerBean implements EventController {

    private final EventService eventService;

    public ResponseObject createEvent(EventCreateDto eventDto) {
        return eventService.createEvent(eventDto);
    }

    public List<EventsInRadiusDto> getEventsWithinRadius(Double latitude, Double longitude, Double radius) {
        return eventService.getEventsWithinRadius(latitude, longitude, radius);
    }

}
