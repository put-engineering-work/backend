package work.user.web.user;


import work.user.dto.ResponseObject;
import work.user.dto.user.*;
import work.user.service.user.UserService;
import work.util.mapstruct.UserMapper;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@RestController
@RequestMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@Api(value = "User", tags = "User")
@Tag(name = "User", description = "User API")
@CrossOrigin
public class UserControllerBean implements UserController {
    private final UserMapper userMapper;
    private final UserService userService;



    @Override
    public ResponseObject tutorRegisterAccount(@Valid RequestUserDto requestUserDto) {
        log.info("UserController ==> tutorRegisterAccount() - start: requestTutorDTO = {}", requestUserDto);
        var newUser = userMapper.requestTutorDtoToTutor(requestUserDto);
        var response = userService.createUser(newUser);

        log.info("UserController ==> tutorRegisterAccount() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject tutorConfirmRegistration(String code) {
        log.info("UserController ==> tutorConfirmRegistration() - start: code = {}", code);
        var responseObject = userService.confirmRegistration(code);
        log.info("UserController ==> tutorConfirmRegistration() - start: responseObject = {}", responseObject);
        return responseObject;
    }


    public ResponseObject login(RequestUserDto userLoginDto) {
        log.info("UserController ==> login() - start: userLoginDto = {}", userLoginDto);
        var token = userService.signin(userMapper.requestTutorDtoToTutor(userLoginDto));
        log.info("UserController ==> login() - end: userLoginDto = {}", token);
        return token;
    }

    public ResponseObject resetPassword(String email) {
        log.info("UserController ==> resetPassword() - start: email = {}", email);
        var response = userService.sendEmailToPasswordReset(email);
        log.info("UserController ==> resetPassword() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject checkCodeForPasswordResetting(String code) {
        log.info("UserController ==> resetPassword() - start: code = {}", code);
        var response = userService.checkCodeForPasswordResetting(code);
        log.info("UserController ==> resetPassword() - start: response = {}", response);
        return response;
    }

    public ResponseObject confirmPasswordResetting(PasswordResetDTO passwordResetDTO) {
        log.info("UserController ==> confirmPasswordResetting() - start: PasswordResetDTO = {}", passwordResetDTO);
        var response = userService.passwordResetting(passwordResetDTO.getCode(), passwordResetDTO.getPassword());
        log.info("UserController ==> confirmPasswordResetting() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject resetPassword(HttpServletRequest request, ChangePasswordDTO password) {
        log.info("UserController ==> resetPassword() - start: request = {}, password = {}", request, password);
        var tutor = userService.getTutorByToken(request);
        var response = userService.resetPassword(tutor, password.getPassword());
        log.info("UserController ==> resetPassword() - end: response = {}", response);
        return response;
    }




}
