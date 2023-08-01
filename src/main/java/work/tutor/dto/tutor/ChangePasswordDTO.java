package work.tutor.dto.tutor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class ChangePasswordDTO {
    @Schema(description = "new password", example = "de2kjn12j4n")
    private String password;

    @JsonCreator
    public ChangePasswordDTO(@JsonProperty("password") String password) {
        this.password = password;
    }
}
