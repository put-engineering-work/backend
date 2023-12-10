package work.user.service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenService {
    private final JwtDecoder jwtDecoder;

    public TokenService(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public Map<String, Object> decodeToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getClaims();
    }
}
