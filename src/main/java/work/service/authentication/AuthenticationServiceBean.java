package work.service.authentication;

import org.springframework.stereotype.Service;
import work.domain.User;
import work.repository.UserRepository;
import work.util.exception.AuthenticationException;
import work.util.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class AuthenticationServiceBean implements AuthenticationService {
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    public AuthenticationServiceBean(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User getUserByToken(HttpServletRequest request) {
        var user = userRepository.findByEmail(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request)));
        if (user.isEmpty()) {
            throw new AuthenticationException("ACCESS_DENIED");
        } else {
            return user.get();
        }
    }
}
