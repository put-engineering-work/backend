package work.web.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.CertainEventDto;
import work.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@CrossOrigin
public class EventControllerBean implements EventController {

    private final EventService eventService;


    @Override
    public ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventDto) {
        return eventService.createEvent(request, eventDto);
    }


    @Override
    public List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, SearchEventDTO searchEventDTO) {
        return eventService.getEventsWithinRadius(request, searchEventDTO);
    }

    @Override
    public CertainEventDto getCertainEvent(UUID eventId) {
        return eventService.getCertainEvent(eventId);
    }

    @Override
    public ResponseObject addCurrentUserToEvent(HttpServletRequest request, UUID eventId) {
        return eventService.addCurrentUserToEvent(request, eventId);
    }

    @Override
    public String isRegisteredInEvent(HttpServletRequest request, UUID eventId) {
        return eventService.isUserRegisteredToEvent(request, eventId);
    }
}
