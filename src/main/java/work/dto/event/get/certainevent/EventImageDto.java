package work.dto.event.get.certainevent;

import work.domain.Event;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.util.UUID;

public record EventImageDto(

        UUID id,

        byte[] image
){
}
