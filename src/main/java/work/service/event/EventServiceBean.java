package work.service.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import work.domain.*;
import work.dto.ResponseObject;
import work.dto.event.create.CreateCommentDto;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.CategoriesDto;
import work.dto.event.get.certainevent.CertainEventDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.CommentDto;
import work.dto.event.get.certainevent.Host;
import work.dto.event.get.certainevent.MembersForUserDto;
import work.dto.event.get.search.EventDto;
import work.repository.*;
import work.service.authentication.AuthenticationService;
import work.service.geodata.GeodataService;
import work.service.util.UtilService;
import work.util.exception.CustomException;
import work.util.mapstruct.CommentMapper;
import work.util.mapstruct.EventMapper;
import work.util.mapstruct.MemberMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
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
    private final CategoryRepository categoryRepository;

    public EventServiceBean(EventRepository eventRepository, EventMapper eventMapper, AuthenticationService authenticationService, MemberRepository memberRepository, GeodataService geodataService, CommentMapper commentMapper, CommentRepository commentRepository, MemberMapper memberMapper, EventImageRepository eventImageRepository, UtilService utilService, CategoryRepository categoryRepository) {
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
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public ResponseObject createEvent(HttpServletRequest request, EventCreateDto eventToCreate) {
        var user = authenticationService.getUserByToken(request);
        var event = eventMapper.fromCreateDto(eventToCreate);

        if (eventToCreate.photos() != null) {
            List<CompletableFuture<EventImage>> futures = eventToCreate.photos().stream()
                    .map(photo -> CompletableFuture.supplyAsync(() -> {
                        byte[] compressedImage = utilService.compressImage(photo, 0.75f); // Сжатие изображения
                        return EventImage.builder()
                                .image(compressedImage)
                                .event(event)
                                .build();
                    }))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            Set<EventImage> eventImages = futures.stream()
                    .map(CompletableFuture::join)
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
        if (eventToCreate.categories() != null && !eventToCreate.categories().isEmpty()) {
            for (var categoryName : eventToCreate.categories()) {
                var category = categoryRepository.findByName(categoryName)
                        .orElseThrow(
                                () -> new CustomException("CATEGORY_NOT_FOUND", HttpStatus.BAD_REQUEST));
                event.getCategories().add(category);
            }
        }
        eventRepository.saveAndFlush(event);

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

        if (StringUtils.isNotBlank(searchEventDTO.eventName())) {
            events = events.stream().filter(e -> e.getName().contains(searchEventDTO.eventName())).toList();
        }

        var response = events.stream()
                .map(eventMapper::eventToEventsInRadiusDto)
                .toList();
        List<Event> finalEvents = events;

        response.forEach(r -> {
            List<CompletableFuture<Void>> futures = r.getEventImages().stream()
                    .map(p -> CompletableFuture.runAsync(() -> p.setImage(utilService.decompressImage(p.getImage()))))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        });

        response.forEach(r -> {
            for (var event : finalEvents) {
                if(event.getId().equals(r.getId())){
                    r.setNumberOfMembers(event.getMembers().size());
                    var categories=event.getCategories().stream().map(EventCategory::getName).toList();
                    r.setCategories(categories);
                    var host=event.getMembers().stream().filter(f->f.getType().equals(AppMemberType.ROLE_HOST)).findFirst().orElseThrow().getUser();
                    r.setHost(new Host(host.getId(), host.getUserDetails().getName(), host.getUserDetails().getLastName()));
                }
            }
        });

        return response;
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
        var categories = event.getCategories().stream().map(EventCategory::getName).toList();
        response.setCategories(categories);
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
    public List<EventDto> getLastNEvents(Integer number) {
        var events = eventRepository.findLastNEvents(number, ZonedDateTime.now());
        var response = events.stream()
                .map(eventMapper::eventToEventDto)
                .toList();
        return getEventDtos(response);
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
        if (StringUtils.isNotBlank(searchEventDTO.eventName())) {
            events = events.stream().filter(e -> e.getName().contains(searchEventDTO.eventName())).toList();
        }
        int totalEvents = events.size();
        return (int) Math.ceil((double) totalEvents / numberOfEventOnPage);
    }

    @Override
    @Transactional
    public List<EventDto> getEventsWithPagination(Integer pageSize, Integer pageNumber, SearchEventDTO searchEventDTO) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Event> eventPage;
        List<Event> result;
        List<EventDto> response;
        if (searchEventDTO.startDate() != null) {
            eventPage = eventRepository.findEventsWithinRadiusWithPagination(searchEventDTO.latitude(),
                    searchEventDTO.longitude(), searchEventDTO.radius(), searchEventDTO.startDate(), pageable);
            eventPage = filterByNameOfEvent(searchEventDTO, eventPage);
        } else {
            eventPage = eventRepository.findEventsWithinRadiusWithPagination(searchEventDTO.latitude(),
                    searchEventDTO.longitude(), searchEventDTO.radius(), pageable);
            eventPage = filterByNameOfEvent(searchEventDTO, eventPage);
        }
        if (searchEventDTO.selectedCategories() != null && !searchEventDTO.selectedCategories().isEmpty()) {
            Set<String> selectedCategoryNames = new HashSet<>(searchEventDTO.selectedCategories());
            result = eventPage.stream()
                    .filter(event -> event.getCategories().stream()
                            .anyMatch(category -> selectedCategoryNames.contains(category.getName())))
                    .toList();
        } else {
            response = eventPage.stream()
                    .map(eventMapper::eventToEventDto)
                    .collect(Collectors.toList());
            return getEventDtos(response);
        }
        response = result.stream()
                .map(eventMapper::eventToEventDto)
                .toList();

        return getEventDtos(response);
    }

    private Page<Event> filterByNameOfEvent(SearchEventDTO searchEventDTO, Page<Event> eventPage) {
        if (StringUtils.isNotBlank(searchEventDTO.eventName())) {
            List<Event> filteredEvents = eventPage.getContent().stream()
                    .filter(e -> e.getName().contains(searchEventDTO.eventName()))
                    .collect(Collectors.toList());

            eventPage = new PageImpl<>(filteredEvents,
                    PageRequest.of(eventPage.getNumber(), eventPage.getSize()),
                    eventPage.getTotalElements());
        }
        return eventPage;
    }

    @Override
    @Transactional
    public List<EventDto> getAllUserEvents(HttpServletRequest request) {
        var user = authenticationService.getUserByToken(request);
        var events = eventRepository.findAllUserEvents(user.getId());
        var response = events.stream().map(eventMapper::eventToEventDto).toList();
        response.forEach(r -> {
            for (var event : events) {
                if(event.getId().equals(r.getId())){
                    r.setNumberOfMembers(event.getMembers().size());
                    var categories=event.getCategories().stream().map(EventCategory::getName).toList();
                    r.setCategories(categories);
                    var host=event.getMembers().stream().filter(f->f.getType().equals(AppMemberType.ROLE_HOST)).findFirst().orElseThrow().getUser();
                    r.setHost(new Host(host.getId(), host.getUserDetails().getName(), host.getUserDetails().getLastName()));
                }
            }
        });
        return getEventDtos(response);
    }

    @Override
    public List<String> getAllCategories() {
        var categories = categoryRepository.findAll();
        var response = new ArrayList<String>();
        for (var category : categories) {
            response.add(category.getName());
        }
        return response;
    }

    @NotNull
    private List<EventDto> getEventDtos(List<EventDto> response) {
        response.forEach(r -> {
            List<CompletableFuture<Void>> futures = r.getEventImages().stream()
                    .map(p -> CompletableFuture.runAsync(() -> p.setImage(utilService.decompressImage(p.getImage()))))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        });

        return response;
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
