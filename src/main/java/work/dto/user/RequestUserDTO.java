package work.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class RequestUserDTO {

    @NotNull
    @Email
    @NotEmpty(message = "EMAIL_MUST_BY_NOT_EMPTY")
    @Schema(description = "Email of an account.", example = "leisure@gmail.com")
    private String email;

    @NotNull
    @NotEmpty(message = "PASSWORD_MUST_BY_NOT_EMPTY")
    @Schema(description = "Password of an account.", example = "123456789")
    private String password;

    private String name;

    private String lastname;
}
