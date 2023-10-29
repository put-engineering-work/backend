package work.user.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Set;


@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class RequestEventDTO {
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
