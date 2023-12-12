package work.service.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
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
import work.service.geodata.GeodataService;
import work.util.exception.CustomException;
import work.util.mapstruct.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceBean implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final AuthenticationService authenticationService;
    private final MemberRepository memberRepository;
    private final GeodataService geodataService;

    public EventServiceBean(EventRepository eventRepository, EventMapper eventMapper, AuthenticationService authenticationService, MemberRepository memberRepository, GeodataService geodataService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.authenticationService = authenticationService;
        this.memberRepository = memberRepository;
        this.geodataService = geodataService;
    }

    public ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventToCreate) {
        var user = authenticationService.getUserByToken(request);
        var event = eventMapper.fromCreateDto(eventToCreate);
        Event finalEvent = event;
        if (event.getAddress() == null || event.getAddress().isEmpty()) {
            geodataService.getAddressFromCoordinates(event.getLocation().getY(), event.getLocation().getX())
                    .subscribe(addressJson -> {
                        String address = extractAddressFromJson(addressJson);
                        finalEvent.setAddress(address);
                        var member = new Member();
                        member.setUser(user);
                        member.setType(AppMemberType.ROLE_HOST);
                        member.setStatus(AppMemberStatus.STATUS_ACTIVE);
                        member.setEvent(eventRepository.saveAndFlush(finalEvent));
                        member = memberRepository.saveAndFlush(member);
                    });
        } else {
            event = eventRepository.saveAndFlush(event);
            var member = new Member();
            member.setUser(user);
            member.setType(AppMemberType.ROLE_HOST);
            member.setStatus(AppMemberStatus.STATUS_ACTIVE);
            member.setEvent(event);
            member = memberRepository.saveAndFlush(member);
        }
        return new ResponseObject(HttpStatus.CREATED, "CREATED", null);
    }

    @Transactional
    public List<EventsInRadiusDto> getEventsWithinRadius(HttpServletRequest request, SearchEventDTO searchEventDTO) {
        List<Event> events;
        if (searchEventDTO.startDate() != null) {
            events = eventRepository.findEventsWithinRadius(searchEventDTO.latitude(), searchEventDTO.longitude(), searchEventDTO.radius(), searchEventDTO.startDate());
        } else {
            events = eventRepository.findEventsWithinRadius(searchEventDTO.latitude(), searchEventDTO.longitude(), searchEventDTO.radius());
        }
        if (searchEventDTO.selectedCategories() != null && !searchEventDTO.selectedCategories().isEmpty()) {
            Set<String> selectedCategoryNames = new HashSet<>(searchEventDTO.selectedCategories());
            events = events.stream()
                    .filter(event -> event.getCategories().stream()
                            .anyMatch(category -> selectedCategoryNames.contains(category.getName())))
                    .collect(Collectors.toList());
        }
        return events.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CertainEventDto getCertainEvent(UUID id) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        var response = eventMapper.toCertainEventDto(event);
        var host = memberRepository.findEventHost(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", HttpStatus.NOT_FOUND));
        var members = event.getMembers().stream().filter(member -> member.getType() != AppMemberType.ROLE_HOST)
                .map(member ->
                        new MembersForUserDto(member.getUser().getId(), member.getUser().getUserDetails().getName(), member.getUser().getUserDetails().getLastName(), member.getType()))
                .collect(Collectors.toSet());
        var responseHost = new Host(host.getUser().getId(), host.getUser().getUserDetails().getName(), host.getUser().getUserDetails().getLastName());
        response.setMembers(members);
        response.setHost(responseHost);
        return response;
    }

    @Override
    public ResponseObject addCurrentUserToEvent(HttpServletRequest request, UUID eventId) {
        var user = authenticationService.getUserByToken(request);
        var event = eventRepository.findById(eventId).orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
        var member = new Member();
        member.setUser(user);
        member.setType(AppMemberType.ROLE_GUEST);
        member.setStatus(AppMemberStatus.STATUS_ACTIVE);
        member.setEvent(eventRepository.saveAndFlush(event));
        member = memberRepository.saveAndFlush(member);
        return new ResponseObject(HttpStatus.OK, "USER_SUCCESSFULLY_ADD", null);
    }

    @Override
    public String isUserRegisteredToEvent(HttpServletRequest request, UUID eventId) {
        var user = authenticationService.getUserByToken(request);
        var event = eventRepository.findEventByIdAndUserId(user.getId(), eventId);
        return event.map(value -> value.getMembers().stream().filter(member -> member.getUser().getId().equals(user.getId())).findFirst().get().getType().name()).orElse("NULL");
    }

    @Override
    public ResponseObject removeCurrentUserFromEvent(HttpServletRequest request, UUID eventId) {
        var user = authenticationService.getUserByToken(request);
        var event = eventRepository.findEventByIdAndUserId(user.getId(), eventId).orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        event.getMembers().stream()
                .filter(member -> member.getUser().equals(user))
                .findFirst().orElseThrow(()->new CustomException("UNAUTHORIZED",HttpStatus.UNAUTHORIZED))
                .setStatus(AppMemberStatus.STATUS_INACTIVE);
        eventRepository.saveAndFlush(event);
        return new ResponseObject(HttpStatus.OK, "SUCCESSFULLY", authenticationService.extractRequestToken(request));
    }

    private String extractAddressFromJson(String addressJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(addressJson);
            if (root.has("results") && root.get("results").isArray()) {
                JsonNode firstResult = root.get("results").get(0);
                if (firstResult.has("formatted_address")) {
                    return firstResult.get("formatted_address").asText();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
