package work.dto.event.get;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SearchEventDTO(
        @Schema(description = "Geographical latitude of the event location", required = true, example = "52.2297")
        Double latitude,
        @Schema(description = "Geographical longitude of the event location", required = true, example = "21.0122")
        Double longitude,

        @Schema(description = "Radius in meters", required = true, example = "500")
        Double radius,
        @Schema(description = "List of selected categories")
        List<String> selectedCategories
) {
}
