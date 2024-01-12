package work.dto.event.get;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;
import java.util.List;

public record SearchEventDTO(
        @Schema(description = "Geographical latitude of the event location", example = "52.2297")
        Double latitude,
        @Schema(description = "Geographical longitude of the event location", example = "21.0122")
        Double longitude,

        @Schema(description = "Radius in meters", example = "500")
        Double radius,

        @Schema(description = "Start event date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        ZonedDateTime startDate,
        
        @Schema(description = "List of selected categories")
        List<String> selectedCategories,

        @Schema(description = "event name")
        String eventName
) {
}
