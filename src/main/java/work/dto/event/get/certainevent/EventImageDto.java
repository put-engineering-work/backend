package work.dto.event.get.certainevent;

import java.util.UUID;

public record EventImageDto(

        UUID id,

        byte[] image
){
}
