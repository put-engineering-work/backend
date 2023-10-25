package work.user.web.user;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import work.user.domain.UserInfo;
import work.user.service.user.UserService;
import work.util.mapstruct.UserMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@Tag(name = "User", description = "User API")
@CrossOrigin
public class UserControllerBean implements UserController {
    private final UserMapper userMapper;
    private final UserService userService;


    @Override
//    @RolesAllowed("User")
    public UserInfo getUserDetails(HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserInfo) {
                UserDetails userInfo = (UserDetails) principal;
                String email = userInfo.getUsername(); // В большинстве случаев, email будет храниться как "username" в UserDetails.

                // Теперь у вас есть адрес электронной почты.
                System.out.println("Email: " + email);
            }
        }

//        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        userService.getUserDetails(1);
        return null;
    }
}
