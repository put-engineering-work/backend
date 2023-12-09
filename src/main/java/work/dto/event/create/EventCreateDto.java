package work.dto.event.create;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;


import java.time.ZonedDateTime;

//@ApiModel(description = "Data required to create a new event")
public record EventCreateDto(
        @Schema(description = "Name of the event", required = true, example = "Rock Concert")
        String name,

        @Schema(description = "Address of the event", required = true, example = "123 Music Street, Warsaw")
        String address,

        @Schema(description = "Description of the event", example = "Music event featuring well-known rock bands")
        String description,

        @Schema(description = "Start date and time of the event", required = true, example = "2023-12-01T20:00:00+01:00")
        ZonedDateTime startDate,

        @Schema(description = "End date and time of the event", required = true, example = "2023-12-01T23:00:00+01:00")
        ZonedDateTime endDate,

        @Schema(description = "Geographical latitude of the event location", required = true, example = "52.2297")
        Double latitude,

        @Schema(description = "Geographical longitude of the event location", required = true, example = "21.0122")
        Double longitude

) {
}

