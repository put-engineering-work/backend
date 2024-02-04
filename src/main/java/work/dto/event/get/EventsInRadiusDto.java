package work.dto.event.get;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import work.domain.EventImage;
import work.dto.event.get.certainevent.EventImageDto;
import work.dto.event.get.certainevent.Host;
import work.dto.event.get.search.EventImagesDto;

import javax.persistence.SequenceGenerators;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class EventsInRadiusDto {
    private UUID id;
    @Schema(description = "Name of the event", example = "Rock Concert")
    private String name;

    @Schema(description = "Address of the event", example = "123 Music Street, Warsaw")
    private String address;

    @Schema(description = "Description of the event", example = "Music event featuring well-known rock bands")
    private String description;

    @Schema(description = "Start date and time of the event", example = "2023-12-01T20:00:00+01:00")
    private ZonedDateTime startDate;

    @Schema(description = "End date and time of the event", example = "2023-12-01T23:00:00+01:00")
    private ZonedDateTime endDate;

    @Schema(description = "Geographical latitude of the event location", example = "52.2297")
    private double latitude;

    @Schema(description = "Geographical longitude of the event location", example = "21.0122")
    private double longitude;

    private Integer numberOfMembers;

//    private List<EventImagesDto> eventImages;

    List<String> categories;

    Host host;

}
