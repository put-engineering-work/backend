package work.util.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;


@Getter
@Setter
public class ErrorDetails {
    private HttpStatus code;
    private String message;
    private String token;

    public ErrorDetails(HttpStatus code, String message, String token) {
        this.code = code;
        this.message = message;
        this.token = token;
    }

}
