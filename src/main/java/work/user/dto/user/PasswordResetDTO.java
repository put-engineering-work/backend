package work.user.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class PasswordResetDTO {
    @NotNull
    @Email
    @NotEmpty(message = "CODE_MUST_BY_NOT_EMPTY")
    @Schema(description = "code")
    private String code;
    @NotNull
    @Email
    @NotEmpty(message = "PASSWORD_MUST_BY_NOT_EMPTY")
    @Schema(description = "password")
    private String password;

}
