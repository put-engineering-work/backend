package work.service.event;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.domain.AppMemberStatus;
import work.domain.AppMemberType;
import work.domain.Event;
import work.domain.Member;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.certainevent.CertainEventDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.Host;
import work.dto.event.get.certainevent.MembersForUserDto;
import work.repository.EventRepository;
import work.repository.MemberRepository;
import work.service.authentication.AuthenticationService;
import work.util.exception.CustomException;
import work.util.mapstruct.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;
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
        var event = eventMapper.fromCreateDto(eventToCreate);
        event = eventRepository.saveAndFlush(event);
        var member = new Member();
        member.setUser(user);
        member.setType(AppMemberType.ROLE_HOST);
        member.setStatus(AppMemberStatus.STATUS_ACTIVE);
        member.setEvent(event);
        member = memberRepository.saveAndFlush(member);
        return new ResponseObject(HttpStatus.CREATED, "CREATED", null);
    }

    @Transactional
    public List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, SearchEventDTO searchEventDTO) {
        var user = authenticationService.getUserByToken(request);
        List<Event> events = eventRepository.findEventsWithinRadius(searchEventDTO.latitude(), searchEventDTO.longitude(), searchEventDTO.radius(), searchEventDTO.selectedCategories());
        return events.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .collect(Collectors.toList());
    }

    @Override
    public CertainEventDto getCertainEvent(UUID id) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        var response = eventMapper.toCertainEventDto(event);
        var host = memberRepository.findEventHost(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", HttpStatus.NOT_FOUND));
        var members = event.getMembers().stream().filter(member -> member.getType() != AppMemberType.ROLE_HOST)
                .map(member ->
                        new MembersForUserDto(member.getUser().getId(), member.getUser().getUserDetails().getName(), member.getUser().getUserDetails().getLastName()))
                .collect(Collectors.toSet());
        var responseHost = new Host(host.getUser().getId(), host.getUser().getUserDetails().getName(), host.getUser().getUserDetails().getLastName());
        response.setMembers(members);
        response.setHost(responseHost);
        return response;
    }
}
