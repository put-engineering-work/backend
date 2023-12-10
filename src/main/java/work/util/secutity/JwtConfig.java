package work.util.secutity;

import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

@Component
public class JwtConfig {
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = "http://localhost:8180/auth/realms/leisurelink-realm/protocol/openid-connect/certs";
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }
}
