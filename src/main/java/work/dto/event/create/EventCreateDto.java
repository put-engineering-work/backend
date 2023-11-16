package work.dto.event.create;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


import java.time.ZonedDateTime;

@ApiModel(description = "Data required to create a new event")
public record EventCreateDto(
        @ApiModelProperty(value = "Name of the event", required = true, example = "Rock Concert")
        String name,

        @ApiModelProperty(value = "Address of the event", required = true, example = "123 Music Street, Warsaw")
        String address,

        @ApiModelProperty(value = "Description of the event", example = "Music event featuring well-known rock bands")
        String description,

        @ApiModelProperty(value = "Start date and time of the event", required = true, example = "2023-12-01T20:00:00+01:00")
        ZonedDateTime startDate,

        @ApiModelProperty(value = "End date and time of the event", required = true, example = "2023-12-01T23:00:00+01:00")
        ZonedDateTime endDate,

        @ApiModelProperty(value = "Geographical latitude of the event location", required = true, example = "52.2297")
        double latitude,

        @ApiModelProperty(value = "Geographical longitude of the event location", required = true, example = "21.0122")
        double longitude

) {
}

