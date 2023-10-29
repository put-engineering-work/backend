package work.user.dto.event;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private Integer id;
    private String name;
    private String address;
    private String description;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    private Set<Integer> memberIds;
    private Set<Integer> eventImageIds;
    private Set<Integer> categoryIds;
    private Set<Integer> commentIds;
}
