package work.util.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;
    private final String message;


    public CustomException(HttpStatus httpStatus, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public CustomException( String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }


    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
