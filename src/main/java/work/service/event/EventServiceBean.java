package work.service.event;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import work.domain.Event;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.repository.EventRepository;
import work.util.mapstruct.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceBean implements EventService{
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public EventServiceBean(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    public ResponseObject createEvent(EventCreateDto eventToCreate) {
        var event=eventMapper.fromCreateDto(eventToCreate);
        event=eventRepository.saveAndFlush(event);
        return new ResponseObject(HttpStatus.CREATED, "CREATED", null);
    }

    public List<EventsInRadiusDto> getEventsWithinRadius(double latitude, double longitude, double radius) {
        List<Event> events = eventRepository.findEventsWithinRadius(latitude, longitude, radius);
        return events.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .collect(Collectors.toList());
    }
}
