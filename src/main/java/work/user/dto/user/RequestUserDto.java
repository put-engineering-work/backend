package work.user.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class RequestUserDto {

    @NotNull
    @Email
    @NotEmpty(message = "EMAIL_MUST_BY_NOT_EMPTY")
    @Schema(description = "Email of an account.", example = "tutor@gmail.com")
    private String email;

    @NotNull
    @NotEmpty(message = "PASSWORD_MUST_BY_NOT_EMPTY")
    @Schema(description = "Password of an account.", example = "123456789")
    private String password;

}
