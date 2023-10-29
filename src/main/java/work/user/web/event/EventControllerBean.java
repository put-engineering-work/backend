package work.user.web.event;

import work.user.dto.ResponseObject;
import work.user.dto.event.RequestEventDTO;
import work.user.service.event.EventService;
import work.util.mapstruct.EventMapper;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/event", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@Api(value = "Event", tags = "Event")
@Tag(name = "Event", description = "Event API")
@CrossOrigin
public class EventControllerBean implements EventController {

    private final EventMapper eventMapper;
    private final EventService eventService;

    @Override
    public ResponseObject createEvent(RequestEventDTO eventDto) {
        log.info("EventController ==> createEvent() - start: eventDto = {}", eventDto);
        var newEvent = eventMapper.requestEventDtoToEvent(eventDto);
        var response = eventService.createEvent(newEvent);

        log.info("EventController ==> createEvent() - end: response = {}", response);
        return response;
    }

}
