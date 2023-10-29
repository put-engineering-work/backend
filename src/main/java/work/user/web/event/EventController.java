package work.user.web.event;


import work.user.dto.ResponseObject;
import work.user.dto.event.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

public interface EventController {

    @PostMapping("/events")
    @Operation(summary = "Create a new event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data provided")
    })
    @ResponseStatus(HttpStatus.CREATED)
    ResponseObject createEvent(@RequestBody @Valid EventDto eventDto);

    @GetMapping("/events/{eventId}")
    @Operation(summary = "Get event details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseObject getEvent(@PathVariable("eventId") Integer eventId);

    @GetMapping("/events")
    @Operation(summary = "List all events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Events list retrieved successfully")
    })
    List<EventDto> listEvents();

    @PutMapping("/events/{eventId}")
    @Operation(summary = "Update event details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid event data provided"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    ResponseObject updateEvent(@PathVariable("eventId") Integer eventId, @RequestBody @Valid EventDto eventDto);

    @DeleteMapping("/events/{eventId}")
    @Operation(summary = "Delete event by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteEvent(@PathVariable("eventId") Integer eventId);


    ResponseObject createEvent(RequestEventDTO eventDto);
}