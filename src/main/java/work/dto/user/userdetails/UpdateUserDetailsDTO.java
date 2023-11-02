package work.dto.user.userdetails;

import lombok.*;

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

    private ZonedDateTime birthDate;
}
