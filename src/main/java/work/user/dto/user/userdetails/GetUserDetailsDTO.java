package work.user.dto.user.userdetails;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class GetUserDetailsDTO {
    @Schema(description = "User id")
    private Integer id;

    private String name;

    private String lastName;

    private String address;

    private String phoneNumber;

    private ZonedDateTime birthDate;
}
