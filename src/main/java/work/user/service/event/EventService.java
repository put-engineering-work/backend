package work.user.service.event;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import work.user.domain.Event;
import work.user.dto.ResponseObject;

public interface EventService {

    ResponseObject createEvent(Event event);

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
        @GetMapping("/{eventId}")
        ResponseEntity<Event> getEvent(@PathVariable Integer eventId);

//    ResponseObject confirmEventCreation(String code);
//
//    ResponseObject addMemberToEvent(Event event, Integer memberId);
//
//    ResponseObject sendCancellationToAttendees(Integer eventId);
//
//    ResponseObject checkEventCode(String code);
//
//    ResponseObject modifyEventDetails(Integer eventId, Event updatedEvent);
//
//    ResponseObject archiveEvent(Integer eventId);
//
//    Event getEventByToken(String token);
//
//    String refreshEventDetails(Integer eventId);
}
