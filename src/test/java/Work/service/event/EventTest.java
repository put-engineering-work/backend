package Work.service.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import work.domain.*;
import work.domain.Event;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.repository.EventImageRepository;
import work.repository.EventRepository;
import work.repository.MemberRepository;
import work.service.authentication.AuthenticationService;
import work.service.event.EventServiceBean;
import work.util.mapstruct.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class EventTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private EventImageRepository eventImageRepository;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceBean eventService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createEvent_ValidData_EventCreated() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        EventCreateDto eventCreateDto = new EventCreateDto(
                "Event Name",
                "Event Address",
                "Event Description",
                "2023-12-01T20:00:00+01:00",
                "2023-12-01T23:00:00+01:00",
                52.2297,
                21.0122,
                Collections.emptyList(),
                new ArrayList<>()
        );

        Event event = Event.builder()
                .id(UUID.randomUUID())
                .name("Event Name")
                .address("Event Address")
                .description("Event Description")
                .startDate(ZonedDateTime.parse("2023-12-01T20:00:00+01:00"))
                .endDate(ZonedDateTime.parse("2023-12-01T23:00:00+01:00"))
                .location(null)
                .members(new HashSet<>())
                .eventImages(new HashSet<>())
                .categories(new HashSet<>())
                .comments(new HashSet<>())
                .build();

        UserDetails userDetails = UserDetails.builder()
                .id(UUID.randomUUID())
                .name("User Name")
                .lastName("User Lastname")
                .address("User Address")
                .phoneNumber("User Phone")
                .birthDate(ZonedDateTime.now())
                .photo(null)
                .user(null)
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .password("password")
                .isActivated(true)
                .appUserRoles(AppUserRole.ROLE_USER)
                .code("UserCode")
                .codeTimeGenerated(ZonedDateTime.now())
                .userDetails(userDetails)
                .build();

        Member member = Member.builder()
                .id(UUID.randomUUID())
                .user(user)
                .event(event)
                .status(AppMemberStatus.STATUS_ACTIVE)
                .type(AppMemberType.ROLE_HOST)
                .messages(new HashSet<>())
                .build();

        when(authenticationService.getUserByToken(request)).thenReturn(user);
        when(eventMapper.fromCreateDto(eventCreateDto)).thenReturn(event);
        when(eventRepository.saveAndFlush(any(Event.class))).thenReturn(event);
        when(memberRepository.saveAndFlush(any(Member.class))).thenReturn(member);

        ResponseObject response = eventService.createEvent(request, eventCreateDto);

        verify(eventRepository, times(2)).saveAndFlush(any(Event.class));
        verify(memberRepository, times(1)).saveAndFlush(any(Member.class));

        assertEquals(HttpStatus.CREATED, response.getCode());
    }
    
}
