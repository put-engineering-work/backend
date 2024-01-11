package work.service.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.domain.*;
import work.dto.ResponseObject;
import work.dto.event.create.CreateCommentDto;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.certainevent.CertainEventDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.CommentDto;
import work.dto.event.get.certainevent.Host;
import work.dto.event.get.certainevent.MembersForUserDto;
import work.repository.CommentRepository;
import work.repository.EventImageRepository;
import work.repository.EventRepository;
import work.repository.MemberRepository;
import work.service.authentication.AuthenticationService;
import work.service.geodata.GeodataService;
import work.service.util.UtilService;
import work.util.exception.CustomException;
import work.util.mapstruct.CommentMapper;
import work.util.mapstruct.EventMapper;
import work.util.mapstruct.MemberMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class EventServiceBean implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final AuthenticationService authenticationService;
    private final MemberRepository memberRepository;
    private final GeodataService geodataService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final MemberMapper memberMapper;

    private final EventImageRepository eventImageRepository;

    private final UtilService utilService;

    public EventServiceBean(EventRepository eventRepository, EventMapper eventMapper, AuthenticationService authenticationService, MemberRepository memberRepository, GeodataService geodataService, CommentMapper commentMapper, CommentRepository commentRepository, MemberMapper memberMapper, EventImageRepository eventImageRepository, UtilService utilService) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.authenticationService = authenticationService;
        this.memberRepository = memberRepository;
        this.geodataService = geodataService;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.memberMapper = memberMapper;
        this.eventImageRepository = eventImageRepository;
        this.utilService = utilService;
    }

    @Transactional
    public ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventToCreate) {
        var user = authenticationService.getUserByToken(request);
        var event = eventMapper.fromCreateDto(eventToCreate);
        if (eventToCreate.photos() != null) {
            Set<EventImage> eventImages = eventToCreate.photos().stream()
                    .map(photo -> {
                        byte[] compressedImage = utilService.compressImage(photo, 0.75f); // Сжатие изображения
                        return EventImage.builder()
                                .image(compressedImage)
                                .event(event)
                                .build();
                    })
                    .collect(Collectors.toSet());

            event.setEventImages(eventImages);
        }

        if (event.getAddress() == null || event.getAddress().isEmpty()) {
            geodataService.getAddressFromCoordinates(event.getLocation().getY(), event.getLocation().getX())
                    .subscribe(addressJson -> {
                        String address = extractAddressFromJson(addressJson);
                        event.setAddress(address);
                        saveEventAndMember(event, user);
                    });
        } else {
            saveEventAndMember(event, user);
        }

        return new ResponseObject(HttpStatus.CREATED, "CREATED", null);
    }

    private void saveEventAndMember(Event event, User user) {
        var savedEvent = eventRepository.saveAndFlush(event);
        var member = new Member();
        member.setUser(user);
        member.setType(AppMemberType.ROLE_HOST);
        member.setStatus(AppMemberStatus.STATUS_ACTIVE);
        member.setEvent(savedEvent);
        memberRepository.saveAndFlush(member);
        if (event.getEventImages() != null) {
            event.getEventImages().forEach(image -> image.setEvent(savedEvent));
            eventImageRepository.saveAll(event.getEventImages());
        }
    }

    @Override
    @Transactional
    public ResponseObject createEventComment(HttpServletRequest request, CreateCommentDto createCommentDto, UUID eventId) {
//        try {
        var user = authenticationService.getUserByToken(request);
        var event = eventRepository.findEventByIdAndUserId(user.getId(), eventId).orElseThrow(() -> new CustomException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED));
        var comment = commentMapper.fromCreateCommentDto(createCommentDto);
        comment.setEvent(event);
        comment.setUser(user);
        comment.setCommentDate(ZonedDateTime.now());
        commentRepository.save(comment);
        return new ResponseObject(HttpStatus.CREATED, "COMMENT_CREATED", authenticationService.extractRequestToken(request));
//        } catch (Exception e) {
//            var event = eventRepository.findById(eventId).orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
//            var comment = commentMapper.fromCreateCommentDto(createCommentDto);
//            comment.setEvent(event);
//            comment.setCommentDate(ZonedDateTime.now());
//            commentRepository.save(comment);
//            return new ResponseObject(HttpStatus.CREATED, "COMMENT_CREATED", null);
//        }

    }

    @Transactional
//    @Cacheable(value = "eventsInRadius", key = "#searchEventDTO")
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
//    @Cacheable(value = "events", key = "#id")
    public CertainEventDto getCertainEvent(UUID id) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        List<CompletableFuture<Void>> futures = event.getEventImages().stream()
                .map(eventImage -> CompletableFuture.runAsync(() -> {
                    byte[] decompressedData = utilService.decompressImage(eventImage.getImage());
                    eventImage.setImage(decompressedData);
                }))
                .toList();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        var response = eventMapper.toCertainEventDto(event);
        var host = memberRepository.findEventHost(id)
                .orElseThrow(() -> new CustomException("NOT_FOUND", HttpStatus.NOT_FOUND));
        var responseHost = new Host(host.getUser().getId(), host.getUser().getUserDetails().getName(), host.getUser().getUserDetails().getLastName());
        response.setHost(responseHost);
        response.setNumberOfMembers(event.getMembers().size());
        return response;
    }

    @Override
    @Transactional
    public List<MembersForUserDto> getMembersForCertainEvent(UUID eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        var members = event.getMembers().stream()
                .filter(member -> member.getType() != AppMemberType.ROLE_HOST && member.getStatus() != AppMemberStatus.STATUS_INACTIVE)
                .toList();
        return members.stream().map(s ->
                new MembersForUserDto(
                        s.getUser().getId(),
                        s.getUser().getUserDetails().getName(),
                        s.getUser().getUserDetails().getLastName(),
                        s.getType())).toList();
    }

    @Override
    @Transactional
    public List<CommentDto> getCommentsForCertainEvent(UUID eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        return commentMapper.toCommentDtoList(event.getComments().stream().toList());
    }

    @Override
    @Transactional
    public ResponseObject addCurrentUserToEvent(HttpServletRequest request, UUID eventId) {
        var user = authenticationService.getUserByToken(request);
        var event = eventRepository.findById(eventId).orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.BAD_REQUEST));
        var dbMember = memberRepository.isMemberExistInEvent(user.getId(), eventId);
        if (dbMember.isPresent()) {
            dbMember.get().setStatus(AppMemberStatus.STATUS_ACTIVE);
            memberRepository.save(dbMember.get());
        } else {
            var member = new Member();
            member.setUser(user);
            member.setType(AppMemberType.ROLE_GUEST);
            member.setStatus(AppMemberStatus.STATUS_ACTIVE);
            member.setEvent(eventRepository.saveAndFlush(event));
            member = memberRepository.saveAndFlush(member);
        }
        return new ResponseObject(HttpStatus.OK, "USER_SUCCESSFULLY_ADD", null);
    }

    @Override
    @Transactional
    public String isUserRegisteredToEvent(HttpServletRequest request, UUID eventId) {
        var user = authenticationService.getUserByToken(request);
        var member = memberRepository.isMemberExistInEvent(user.getId(), eventId);
        if (member.isPresent()) {
            if (member.get().getStatus().equals(AppMemberStatus.STATUS_INACTIVE)) {
                return "NULL";
            } else return member.get().getType().toString();
        } else return "NULL";
    }

    @Override
    @Transactional
    public ResponseObject removeCurrentUserFromEvent(HttpServletRequest request, UUID eventId) {
        var user = authenticationService.getUserByToken(request);
        var event = eventRepository.findEventByIdAndUserId(user.getId(), eventId).orElseThrow(() -> new CustomException("EVENT_NOT_FOUND", HttpStatus.NOT_FOUND));
        event.getMembers().stream()
                .filter(member -> member.getUser().equals(user))
                .findFirst().orElseThrow(() -> new CustomException("UNAUTHORIZED", HttpStatus.UNAUTHORIZED))
                .setStatus(AppMemberStatus.STATUS_INACTIVE);
        eventRepository.saveAndFlush(event);
        return new ResponseObject(HttpStatus.OK, "SUCCESSFULLY", authenticationService.extractRequestToken(request));
    }

    @Override
    public List<EventsInRadiusDto> getLastNEvents(Integer number) {
        var events = eventRepository.findLastNEvents(number, ZonedDateTime.now());
        return events.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Integer getNumberOfPages(Integer numberOfEventOnPage, SearchEventDTO searchEventDTO) {
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
        int totalEvents = events.size();
        return (int) Math.ceil((double) totalEvents / numberOfEventOnPage);
    }

    @Override
    @Transactional
    public List<EventsInRadiusDto> getEventsWithPagination(Integer pageSize, Integer pageNumber, SearchEventDTO searchEventDTO) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Event> eventPage;
        List<Event> result;
        if (searchEventDTO.startDate() != null) {
            eventPage = eventRepository.findEventsWithinRadiusWithPagination(searchEventDTO.latitude(),
                    searchEventDTO.longitude(), searchEventDTO.radius(), searchEventDTO.startDate(), pageable);
        } else {
            eventPage = eventRepository.findEventsWithinRadiusWithPagination(searchEventDTO.latitude(),
                    searchEventDTO.longitude(), searchEventDTO.radius(), pageable);
        }
        if (searchEventDTO.selectedCategories() != null && !searchEventDTO.selectedCategories().isEmpty()) {
            Set<String> selectedCategoryNames = new HashSet<>(searchEventDTO.selectedCategories());
            result = eventPage.stream()
                    .filter(event -> event.getCategories().stream()
                            .anyMatch(category -> selectedCategoryNames.contains(category.getName())))
                    .toList();
        } else {
            return eventPage.stream()
                    .map(eventMapper::eventToEventsInRadiusDto)
                    .collect(Collectors.toList());
        }
        return result.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventsInRadiusDto> getAllUserEvents(HttpServletRequest request) {
        var user = authenticationService.getUserByToken(request);
        var events = eventRepository.findAllUserEvents(user.getId());
        return events.stream().map(eventMapper::eventToEventsInRadiusDto).collect(Collectors.toList());
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
