package work.dto.event.get.certainevent;

import java.util.UUID;

public record Host(
        UUID id,
        String name,
        String lastname
) {
}
