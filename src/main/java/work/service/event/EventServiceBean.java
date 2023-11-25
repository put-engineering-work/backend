package work.service.event;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import work.domain.AppMemberStatus;
import work.domain.AppMemberType;
import work.domain.Event;
import work.domain.Member;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.repository.EventRepository;
import work.repository.MemberRepository;
import work.service.authentication.AuthenticationService;
import work.util.mapstruct.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServiceBean implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final AuthenticationService authenticationService;
    private final MemberRepository memberRepository;


    public ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventToCreate) {
        var user = authenticationService.getUserByToken(request);
        var member = new Member();
        member.setUser(user);
        member.setType(AppMemberType.ROLE_HOST);
        member.setStatus(AppMemberStatus.STATUS_ACTIVE);
        member = memberRepository.saveAndFlush(member);
        var event = eventMapper.fromCreateDto(eventToCreate);
        event.getMembers().add(member);
        event = eventRepository.saveAndFlush(event);
        return new ResponseObject(HttpStatus.CREATED, "CREATED", null);
    }

    public List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, double latitude, double longitude, double radius) {
        var user = authenticationService.getUserByToken(request);
        List<Event> events = eventRepository.findEventsWithinRadius(latitude, longitude, radius);
        return events.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .collect(Collectors.toList());
    }
}
