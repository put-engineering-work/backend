package work.tutor.web.tutor.tutorendpoints;


import work.tutor.dto.ResponseObject;
import work.tutor.dto.tutor.*;
import work.tutor.service.UserService;
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
@RequestMapping(value = "/tutor", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
@Slf4j
@Api(value = "Tutor", tags = "Tutor")
@Tag(name = "Tutor", description = "Tutor API")
@CrossOrigin
public class TutorControllerBean implements TutorController {
    private final UserMapper userMapper;
    private final UserService userService;



    @Override
    public ResponseObject tutorRegisterAccount(@Valid RequestUserDto requestUserDto) {
        log.info("TutorController ==> tutorRegisterAccount() - start: requestTutorDTO = {}", requestUserDto);
        var newTutor = userMapper.requestTutorDtoToTutor(requestUserDto);
        var response = userService.createUser(newTutor);

        log.info("TutorController ==> tutorRegisterAccount() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject tutorConfirmRegistration(String code) {
        log.info("TutorController ==> tutorConfirmRegistration() - start: code = {}", code);
        var responseObject = userService.confirmRegistration(code);
        log.info("TutorController ==> tutorConfirmRegistration() - start: responseObject = {}", responseObject);
        return responseObject;
    }


    public ResponseObject login(RequestUserDto userLoginDto) {
        log.info("TutorController ==> login() - start: userLoginDto = {}", userLoginDto);
        var token = userService.signin(userMapper.requestTutorDtoToTutor(userLoginDto));
        log.info("TutorController ==> login() - end: userLoginDto = {}", token);
        return token;
    }

    public ResponseObject resetPassword(String email) {
        log.info("TutorController ==> resetPassword() - start: email = {}", email);
        var response = userService.sendEmailToPasswordReset(email);
        log.info("TutorController ==> resetPassword() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject checkCodeForPasswordResetting(String code) {
        log.info("TutorController ==> resetPassword() - start: code = {}", code);
        var response = userService.checkCodeForPasswordResetting(code);
        log.info("TutorController ==> resetPassword() - start: response = {}", response);
        return response;
    }

    public ResponseObject confirmPasswordResetting(PasswordResetDTO passwordResetDTO) {
        log.info("TutorController ==> confirmPasswordResetting() - start: PasswordResetDTO = {}", passwordResetDTO);
        var response = userService.passwordResetting(passwordResetDTO.getCode(), passwordResetDTO.getPassword());
        log.info("TutorController ==> confirmPasswordResetting() - end: response = {}", response);
        return response;
    }

    @Override
    public ResponseObject resetPassword(HttpServletRequest request, ChangePasswordDTO password) {
        log.info("TutorController ==> updateEvent() - start: request = {}, password = {}", request, password);
        var tutor = userService.getTutorByToken(request);
        var response = userService.resetPassword(tutor, password.getPassword());
        log.info("TutorController ==> removeEvent() - end: response = {}", response);
        return response;
    }




}
