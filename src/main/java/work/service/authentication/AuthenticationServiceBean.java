package work.service.authentication;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Service;
import work.domain.User;
import work.repository.UserRepository;
import work.util.exception.AuthenticationException;
import work.util.security.JwtTokenProvider;

import javax.servlet.http.HttpServletRequest;

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
            throw new AuthenticationException();
        } else {
            return user.get();
        }
    }

    @Override
    public User getUserByToken(String token) {
        var user = userRepository.findByEmail(jwtTokenProvider.getUsername(token));
        if (user.isEmpty()) {
            throw new AuthenticationException();
        } else {
            return user.get();
        }
    }

    @Override
    public User getUserByToken(ServerHttpRequest request) {
        var token = extractToken(request);
        var user = userRepository.findByEmail(jwtTokenProvider.getUsername(token));
        if (user.isEmpty()) {
            throw new AuthenticationException();
        } else {
            return user.get();
        }
    }

    @Override
    public String extractRequestToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.replace("Bearer ", "");
        }
        return null;
    }

    private String extractToken(ServerHttpRequest request) {
        String tokenParam = request.getURI().getQuery();
        if (tokenParam != null && tokenParam.startsWith("token=")) {
            return tokenParam.substring(6);
        }
        return null;
    }
}
