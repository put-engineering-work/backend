package work.web.event;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.CertainEventDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@Api(value = "Event", tags = "Event")
@Tag(name = "Event", description = "Event API")
public interface EventController {
    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data provided")
    })
    @PostMapping("/create")
    ResponseObject createEvent(HttpServletRequest request, @RequestBody EventCreateDto eventDto);


    @Operation(summary = "Get events within a specific radius")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of events within the specified radius"),
            @ApiResponse(responseCode = "400", description = "Invalid coordinates or radius provided")
    })
    @PostMapping("/search")
    List<EventsInRadiusDto> getEventsWithinRadius(
            HttpServletRequest request,
            @RequestBody SearchEventDTO searchEventDTO);

    @Operation(summary = "Get certain event by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event data"),
                    @ApiResponse(responseCode = "404", description = "Event with this id not found")
            }
    )
    @GetMapping("/event/{eventId}")
    CertainEventDto getCertainEvent(@PathVariable("eventId") UUID eventId);

    @Operation(summary = "Add user to event")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User added"),
                    @ApiResponse(responseCode = "404", description = "EVENT_NOT_FOUND")
            }
    )
    @PostMapping("/{eventId}/add-user")
    ResponseObject addCurrentUserToEvent(HttpServletRequest request, @PathVariable("eventId") UUID eventId);

    @GetMapping("/is-registered/{eventId}")
    String isRegisteredInEvent(HttpServletRequest request, @PathVariable("eventId") UUID eventId);
}
