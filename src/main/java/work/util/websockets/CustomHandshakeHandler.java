package work.util.websockets;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import work.domain.User;
import work.service.authentication.AuthenticationService;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private final AuthenticationService authenticationService;

    public CustomHandshakeHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String token = extractToken(request);
        if (token != null) {
            User user = authenticationService.getUserByToken(token);
            if (user != null) {
                return user::getEmail;
            }
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

