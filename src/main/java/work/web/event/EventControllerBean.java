package work.web.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import work.dto.ResponseObject;
import work.dto.event.create.CreateCommentDto;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.CertainEventDto;
import work.dto.event.get.certainevent.CommentDto;
import work.dto.event.get.certainevent.MembersForUserDto;
import work.dto.event.get.search.EventDto;
import work.dto.event.get.search.NumberOfPages;
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
    public ResponseObject createEventComment(HttpServletRequest request, CreateCommentDto createCommentDto, UUID eventId) {
        return eventService.createEventComment(request, createCommentDto, eventId);
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
    public List<MembersForUserDto> getMembersForCertainEvent(UUID eventId) {
        return eventService.getMembersForCertainEvent(eventId);
    }

    @Override
    public List<CommentDto> getCommentsForCertainEvent(UUID eventId) {
        return eventService.getCommentsForCertainEvent(eventId);
    }

    @Override
    public ResponseObject addCurrentUserToEvent(HttpServletRequest request, UUID eventId) {
        return eventService.addCurrentUserToEvent(request, eventId);
    }

    @Override
    public ResponseObject isRegisteredInEvent(HttpServletRequest request, UUID eventId) {
        return new ResponseObject(HttpStatus.OK, eventService.isUserRegisteredToEvent(request, eventId), null);
    }

    @Override
    public ResponseObject removeCurrentUserFromEvent(HttpServletRequest request, UUID eventId) {
        return eventService.removeCurrentUserFromEvent(request, eventId);
    }

    @Override
    public List<EventDto> getLastNEvents(Integer number) {
        return eventService.getLastNEvents(number);
    }

    @Override
    public NumberOfPages getNumberOfPages(Integer numberOfEventOnPage, SearchEventDTO searchEventDTO) {
        return eventService.getNumberOfPages(numberOfEventOnPage, searchEventDTO);
    }

    @Override
    public List<EventDto> getEventsWithPagination(Integer pageSize, Integer pageNumber, SearchEventDTO searchEventDTO) {
        return eventService.getEventsWithPagination(pageSize, pageNumber, searchEventDTO);
    }

    @Override
    public List<EventDto> getAllUserEvents(HttpServletRequest request) {
        return eventService.getAllUserEvents(request);
    }

    @Override
    public List<String> getAllCategories() {
        return eventService.getAllCategories();
    }
}
