package work.web.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import work.dto.ResponseObject;
import work.dto.event.create.CreateCommentDto;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;
import work.dto.event.get.certainevent.CertainEventDto;
import work.dto.event.get.certainevent.CommentDto;
import work.dto.event.get.certainevent.MembersForUserDto;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

//@Api(value = "Event", tags = "Event")
@Tag(name = "Event", description = "Event API")
public interface EventController {
    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data provided")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/create")
    ResponseObject createEvent(HttpServletRequest request, @ModelAttribute EventCreateDto eventDto);

    @Operation(summary = "Create comment to event")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "COMMENT_CREATED")
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/create/{eventId}/comment")
    ResponseObject createEventComment(HttpServletRequest request, @RequestBody CreateCommentDto createCommentDto, @PathVariable("eventId") UUID eventId);

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

    @Operation(summary = "Get event members for certain event by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event data"),
                    @ApiResponse(responseCode = "404", description = "Event with this id not found")
            }
    )
    @GetMapping("/event/{eventId}/members")
    List<MembersForUserDto> getMembersForCertainEvent(@PathVariable("eventId") UUID eventId);

    @Operation(summary = "Get event comments for certain event by id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Event data"),
                    @ApiResponse(responseCode = "404", description = "Event with this id not found")
            }
    )
    @GetMapping("/event/{eventId}/comments")
    List<CommentDto> getCommentsForCertainEvent(@PathVariable("eventId") UUID eventId);

    @Operation(summary = "Add user to event")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User added"),
                    @ApiResponse(responseCode = "404", description = "EVENT_NOT_FOUND")
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{eventId}/add-user")
    ResponseObject addCurrentUserToEvent(HttpServletRequest request, @PathVariable("eventId") UUID eventId);

    @Operation(summary = "Is user registered to event")
    @ApiResponses(
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/is-registered/{eventId}")
    ResponseObject isRegisteredInEvent(HttpServletRequest request, @PathVariable("eventId") UUID eventId);

    @Operation(summary = "Remove current user from event")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "SUCCESSFULLY"),
                    @ApiResponse(responseCode = "403", description = "UNAUTHORIZED")
            }
    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/remove-me/{eventId}")
    ResponseObject removeCurrentUserFromEvent(HttpServletRequest request, @PathVariable("eventId") UUID eventId);


    @Operation(summary = "Get last N events.", description ="For main page, just write expected offset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400")
    })
    @GetMapping("/last/{number}")
    @PermitAll
    List<EventsInRadiusDto> getLastNEvents(@PathVariable("number") Integer number);


    @Operation(summary = "Get number of pages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/number/{numberOfEventOnPage}")
    Integer getNumberOfPages(@PathVariable("numberOfEventOnPage") Integer numberOfEventOnPage, SearchEventDTO searchEventDTO);


    @Operation(summary = "Get number of pages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK")
    })
    @PostMapping("/pageable/{pageSize}/{pageNumber}")
    List<EventsInRadiusDto> getEventsWithPagination(@PathVariable("pageSize") Integer pageSize, @PathVariable("pageNumber") Integer pageNumber, SearchEventDTO searchEventDTO);
}