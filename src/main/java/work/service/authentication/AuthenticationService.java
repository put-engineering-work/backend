package work.service.authentication;

import work.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface AuthenticationService {
    User getUserByToken(HttpServletRequest request);
    String extractRequestToken(HttpServletRequest request);
    Random RANDOM = new SecureRandom();
    String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    static String generateRandomAlphanumeric(int length) {
        return IntStream.range(0, length)
                .map(i -> ALPHANUMERIC_CHARACTERS.charAt(RANDOM.nextInt(ALPHANUMERIC_CHARACTERS.length())))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }
}
