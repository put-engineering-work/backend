package work.dto.event.get;

import io.swagger.v3.oas.annotations.media.Schema;
import work.domain.EventImage;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record EventsInRadiusDto(
        UUID id,
        @Schema(description = "Name of the event", example = "Rock Concert")
        String name,

        @Schema(description = "Address of the event", example = "123 Music Street, Warsaw")
        String address,

        @Schema(description = "Description of the event", example = "Music event featuring well-known rock bands")
        String description,

        @Schema(description = "Start date and time of the event", example = "2023-12-01T20:00:00+01:00")
        ZonedDateTime startDate,

        @Schema(description = "End date and time of the event", example = "2023-12-01T23:00:00+01:00")
        ZonedDateTime endDate,

        @Schema(description = "Geographical latitude of the event location", example = "52.2297")
        double latitude,

        @Schema(description = "Geographical longitude of the event location", example = "21.0122")
        double longitude,

        Set<EventImage> images
//        ,

//        Set<Member> members,
//        Set<EventImage> eventImages,
//        Set<EventCategory> categories,
//
//        Set<Comment> comments
) {
}
