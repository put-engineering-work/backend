package work.web.user;


import org.springframework.web.multipart.MultipartFile;
import work.dto.ResponseObject;
import work.dto.user.*;
import work.dto.user.userdetails.GetUserDetailsDTO;
import work.dto.user.userdetails.UpdateUserDetailsDTO;
import work.service.authentication.AuthenticationService;
import work.service.user.UserService;
import work.util.mapstruct.UserMapper;
//import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
//@Api(value = "User", tags = "User")
@Tag(name = "User", description = "User API")
@CrossOrigin
public class UserControllerBean implements UserController {
    private final UserMapper userMapper;
    private final UserService userService;

    private final AuthenticationService authenticationService;

    @Override
    public ResponseObject userRegisterAccount(@Valid RequestUserDTO requestUserDto) {
        var newUser = userMapper.requestUserDtoToUser(requestUserDto);
        return userService.createUser(newUser);
    }

    @Override
    public ResponseObject login(RequestLoginDTO userLoginDto) {
        return userService.signin(userMapper.fromRequestUserDto(userLoginDto));
    }

    public ResponseObject resetPassword(String email) {
        log.info("UserController ==> resetPassword() - start: email = {}", email);
        var response = userService.sendEmailToPasswordReset(email);
        log.info("UserController ==> resetPassword() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject checkCodeForPasswordResetting(String code) {
        return userService.checkCodeForPasswordResetting(code);
    }

    @Override
    public ResponseObject confirmPasswordResetting(PasswordResetDTO passwordResetDTO) {
        return userService.passwordResetting(passwordResetDTO.getCode(), passwordResetDTO.getPassword());
    }

    @Override
    public ResponseObject resetPassword(HttpServletRequest request, ChangePasswordDTO password) {
        var tutor = authenticationService.getUserByToken(request);
        return userService.resetPassword(tutor, password.getPassword());
    }

    @Override
    public GetUserDetailsDTO getUserDetails(HttpServletRequest request) {
        return userService.getUserDetails(authenticationService.getUserByToken(request).getId());
    }

    @Override
    public ResponseObject updateUserDetails(HttpServletRequest request, UpdateUserDetailsDTO detailsDTO) {
        return userService.updateUserDetails(detailsDTO, authenticationService.getUserByToken(request).getId());
    }

    @Override
    public ResponseObject updateUserImage(HttpServletRequest request, MultipartFile photo) {
        return userService.updateUserImage(authenticationService.getUserByToken(request).getId(), photo);
    }

    @Override
    public UUID getMyId(HttpServletRequest request) {
        return userService.getMyId(request);
    }
}
