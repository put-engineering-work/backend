package work.user.web.user;


import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;

import work.user.service.TokenService;
import work.user.service.user.UserService;
import work.util.mapstruct.UserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@Tag(name = "User", description = "User API")
@CrossOrigin
public class UserControllerBean implements UserController {
    private final UserMapper userMapper;
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    @RolesAllowed("User")
    public String getUserDetails(HttpServletRequest request) {
        var tokenClaims = tokenService.decodeToken(request.getHeader("Authorization").replace("Bearer ", ""));
        String email = (String) tokenClaims.get("email");

        // Далее, вы можете использовать полученный email для выполнения действий в вашем сервисе.

        return email;
    }

    @Override
    @PermitAll
    public Object newdrwadsad() {
        return null;
    }
}
