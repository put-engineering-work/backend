package work.user.service.event;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import work.user.domain.Event;
import work.user.dto.ResponseObject;
import work.user.repository.EventRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Service
//@RequestMapping("/api/events")
//@RestController
public class EventServiceBean implements EventService {
    private final EventRepository eventRepository;

    private final EventService eventService;

//    @Autowired
//    public EventControllerBean(EventService eventService) {
//        this.eventService = eventService;
//    }



    @Override
    public ResponseObject createEvent(Event event) {
        log.debug("EventService ==> createEvent() - start: event = {}", event);
        var eventFromDb = eventRepository.findByTitle(event.getName());

        if (eventFromDb.isEmpty()) {
            String code = RandomStringUtils.randomAlphanumeric(30, 30);
            event.setCode(code);
            event.setCreatedAt(ZonedDateTime.now());
            eventRepository.save(event);
            var response = new ResponseObject(HttpStatus.ACCEPTED, "EVENT_CREATED", null);
            log.debug("EventService ==> createEvent() - end: response = {}", response);
            return response;
        } else {
            var response = new ResponseObject(HttpStatus.UNPROCESSABLE_ENTITY, "EVENT_ALREADY_EXISTS", null);
            log.debug("EventService ==> createEvent() - end: response = {}", response);
            return response;
        }
    }

//    @Override
//    public ResponseObject confirmEventCreation(String code) {
//        log.debug("EventService ==> confirmEventCreation() - start: code = {}", code);
//        var optionalEvent = eventRepository.findByCode(code);
//        if (optionalEvent.isPresent()) {
//            long hours = getHours(optionalEvent);
//            Event event = optionalEvent.get();
//            if (hours < 24) {
//                event.setCode(null);
//                event.setEventTimeGenerated(null);
//                eventRepository.save(event);
//                log.debug("EventService ==> confirmEventCreation() - end: code = {}, message = {}", HttpStatus.OK, "Event creation confirmed");
//                return new ResponseObject(HttpStatus.OK, "EVENT_CREATION_CONFIRMED", null);
//            } else {
//                return new ResponseObject(HttpStatus.BAD_REQUEST, "EVENT_CREATION_EXPIRED", null);
//            }
//        } else {
//            log.debug("EventService ==> confirmEventCreation() - end: code = {}, message = {}", HttpStatus.BAD_REQUEST, "Invalid event code");
//            return new ResponseObject(HttpStatus.BAD_REQUEST, "INVALID_EVENT_CODE", null);
//        }
//    }

//    private long getHours(Optional<Event> optionalEvent) {
//        Instant eventTimeGenerated = optionalEvent.get().getEventTimeGenerated().toInstant();
//        Instant now = Instant.now();
//
//        Duration duration = Duration.between(eventTimeGenerated, now);
//        long hours = duration.toHours();
//        return hours;
//    }

//    @Override
//    public ResponseObject addMemberToEvent(Event event, Integer memberId) {
//        log.debug("EventService ==> addMemberToEvent() - start: event = {}, memberId = {}", event, memberId);
//
//        Optional<Member> optionalMember = memberRepository.findById(memberId);
//
//        if (optionalMember.isEmpty()) {
//            log.debug("EventService ==> addMemberToEvent() - end: status = {}, message = {}", HttpStatus.BAD_REQUEST, "Member not found");
//            return new ResponseObject(HttpStatus.BAD_REQUEST, "MEMBER_NOT_FOUND", null);
//        }
//
//        Member member = optionalMember.get();
//
//        event.addMember(member);
//
//        eventRepository.save(event);
//
//        log.debug("EventService ==> addMemberToEvent() - end: status = {}, message = {}", HttpStatus.OK, "Member added to event");
//        return new ResponseObject(HttpStatus.OK, "MEMBER_ADDED_TO_EVENT", null);
//    }
//
    @Override
    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEvent(@PathVariable Integer eventId) {
        log.debug("EventController ==> getEvent() - start: eventId = {}", eventId);

        Optional<Event> optionalEvent = eventService.findEventById(eventId);

        if (optionalEvent.isPresent()) {
            return ResponseEntity.ok(optionalEvent.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
