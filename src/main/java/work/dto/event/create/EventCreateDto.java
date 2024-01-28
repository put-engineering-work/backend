package work.dto.event.create;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record EventCreateDto(
        @Schema(description = "Name of the event", example = "Rock Concert")
        String name,

        @Schema(description = "Address of the event", example = "123 Music Street, Warsaw")
        String address,

        @Schema(description = "Description of the event", example = "Music event featuring well-known rock bands")
        String description,

        @Schema(description = "Start date and time of the event", example = "2023-12-01T20:00:00+01:00")
        String startDate,

        @Schema(description = "End date and time of the event", example = "2023-12-01T23:00:00+01:00")
        String endDate,

        @Schema(description = "Geographical latitude of the event location", example = "52.2297")
        Double latitude,

        @Schema(description = "Geographical longitude of the event location", example = "21.0122")
        Double longitude,

        @Schema(description = "list of categories")
        List<String> categories,

        @Schema(description = "Photo")
        List<MultipartFile> photos
) {
}

