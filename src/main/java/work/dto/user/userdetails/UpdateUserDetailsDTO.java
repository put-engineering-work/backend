package work.dto.user.userdetails;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class UpdateUserDetailsDTO {
    private String name;

    private String lastName;

    private String address;

    private String phoneNumber;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private ZonedDateTime birthDate;
}
