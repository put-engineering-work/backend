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
import work.dto.event.get.SearchEventDTO;
import work.service.event.EventService;
import work.service.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/events", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@CrossOrigin
public class EventControllerBean implements EventController {

    private final EventService eventService;
    private final UserService userService;


    public ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventDto) {
        return eventService.createEvent(request, eventDto);
    }


    public List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, SearchEventDTO searchEventDTO) {
        return eventService.getEventsWithinRadius(request, searchEventDTO);
    }

}
