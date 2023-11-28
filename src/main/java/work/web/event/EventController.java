package work.web.event;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import work.dto.ResponseObject;
import work.dto.event.create.EventCreateDto;
import work.dto.event.get.EventsInRadiusDto;
import work.dto.event.get.SearchEventDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
}
