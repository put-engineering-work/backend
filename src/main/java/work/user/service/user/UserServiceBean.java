package work.user.service.user;

import work.user.domain.AppUserRole;
import work.user.domain.User;
import work.user.dto.ResponseObject;
import work.user.dto.user.UserToken;
import work.user.repository.UserRepository;
import work.util.exception.AuthenticationException;
import work.util.exception.UserNotFoundException;
import work.util.secutity.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;


@AllArgsConstructor
@Slf4j
@Service
public class UserServiceBean implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;


    @Override
    public ResponseObject createUser(User user) {
        log.debug("TutorService ==> createUser() - start: tutor = {}", user);
        var userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb.isEmpty()) {
            user.setIsActivated(Boolean.FALSE);
            String code = RandomStringUtils.randomAlphanumeric(30, 30);
            user.setCode(code);
            user.setAppUserRoles(AppUserRole.ROLE_USER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCodeTimeGenerated(ZonedDateTime.now());
            userRepository.save(user);
            var response = new ResponseObject(HttpStatus.ACCEPTED, "USER_CREATED", null);
            log.debug("TutorService ==> createUser() - end: tutor = {}", response);
            return response;
        } else {
            var response = new ResponseObject(HttpStatus.UNPROCESSABLE_ENTITY, "USER_ALREADY_EXIST", null);
            log.debug("TutorService ==> createUser() - end: tutor = {}", response);
            return response;
        }
    }

    @Override
    public ResponseObject confirmRegistration(String code) {
        log.debug("TutorService ==> confirmRegistration() - start: code = {}", code);
        var optionalTutor = userRepository.findByCode(code);
        if (optionalTutor.isPresent()) {
            long hours = getHours(optionalTutor);
            User user = optionalTutor.get();
            if (hours < 24) {
                user.setIsActivated(Boolean.TRUE);
                user.setCode(null);
                user.setCodeTimeGenerated(null);
                userRepository.save(user);
                String token = jwtTokenProvider.createToken(
                        user.getEmail(),
                        new LinkedList<>(Collections.singletonList(user.getAppUserRoles())));
                var userToken = new UserToken();
                userToken.setToken(token);
                userToken.setAppUserRole(user.getAppUserRoles());
                log.debug("TutorService ==> confirmRegistration() - end: code = {}, message = {}, token = {}", HttpStatus.OK, "Registration confirmed", token);
                return new ResponseObject(HttpStatus.OK, "REGISTRATION_CONFIRMED", token);
            } else {

                return new ResponseObject(HttpStatus.OK, "VERIFICATION_LINK_WAS_SEND_AGAIN", null);
            }
        } else {
            log.debug("TutorService ==> confirmRegistration() - end: code = {}, message = {}, token = {}", HttpStatus.BAD_REQUEST, "Invalid registration code", null);
            return new ResponseObject(HttpStatus.BAD_REQUEST, "INVALID_REGISTRATION_CODE", null);
        }
    }


    @Override
    public ResponseObject signin(User user) {
        log.debug("TutorService ==> signin() - start: user = {}", user);
        UserToken userToken;
        Optional<User> userLoginData = userRepository.findByEmail(user.getEmail());
        if (userLoginData.isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "USER_WAS_NOT_REGISTERED", null);
        }
//        else if (!tutorLoginData.get().getIsActivated()) {
//            try {
//                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
//                String code = RandomStringUtils.randomAlphanumeric(30, 30);
//                tutorLoginData.get().setCode(code);
//                tutorLoginData.get().setCodeTimeGenerated(ZonedDateTime.now());
//                tutorLoginData.get().setPassword(passwordEncoder.encode(user.getPassword()));
//
//                userRepository.save(tutorLoginData.get());
//
//                return new ResponseObject(HttpStatus.UNPROCESSABLE_ENTITY, "VERIFICATION_CODE_WAS_SENT_ONCE_AGAIN", null);
//            } catch (Exception e) {
//                return new ResponseObject(HttpStatus.UNAUTHORIZED, "WRONG_DATA", null);
//            }
//        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            String token = jwtTokenProvider.createToken(
                    userLoginData.get().getEmail(),
                    new LinkedList<>(Collections.singletonList(userLoginData.get().getAppUserRoles())));
            userToken = new UserToken();
            userToken.setToken(token);
            userToken.setAppUserRole(userLoginData.get().getAppUserRoles());
            log.debug("TutorService ==> signin() - end: userToken = {}", userToken);
            return new ResponseObject(HttpStatus.ACCEPTED, "CORRECT_LOGIN_DATA", token, userLoginData.get().getAppUserRoles());
        } catch (Exception e) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "WRONG_DATA", null);
        }

    }

    @Override
    public ResponseObject sendEmailToPasswordReset(String email) {
        log.debug("TutorService ==> sendEmailToPasswordReset() - start: email = {}", email);
        var tutor = userRepository.findByEmail(email);
        if (tutor.isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "WRONG_DATA", null);
        } else if (!tutor.get().getIsActivated()) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "ACCOUNT_IS_NOT_ACTIVATED", null);
        } else {
            String code = RandomStringUtils.randomAlphanumeric(30, 30);
            tutor.get().setCode(code);
            tutor.get().setCodeTimeGenerated(ZonedDateTime.now());
            userRepository.save(tutor.get());

            log.debug("TutorService ==> sendEmailToPasswordReset() - end:  HttpStatus = {}, message = {}, token = {}", HttpStatus.ACCEPTED, "EMAIL_SENT", null);
            return new ResponseObject(HttpStatus.ACCEPTED, "EMAIL_SENT", null);
        }
    }

    @Override
    public ResponseObject checkCodeForPasswordResetting(String code) {
        log.debug("TutorService ==> passwordResetting() - start:  code = {}", code);
        var tutor = userRepository.findByCode(code);
        if (tutor.isEmpty()) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "INVALID_CODE", null);
        } else if (!tutor.get().getIsActivated()) {
            return new ResponseObject(HttpStatus.UNPROCESSABLE_ENTITY, "ACCOUNT_IS_NOT_ACTIVATED", null);
        }
        long hours = getHours(tutor);
        if (hours < 4) {
            String newCode = RandomStringUtils.randomAlphanumeric(30, 30);
            tutor.get().setCode(newCode);
            tutor.get().setCodeTimeGenerated(ZonedDateTime.now());
            userRepository.save(tutor.get());
            return new ResponseObject(HttpStatus.ACCEPTED, newCode, null);
        } else {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "CODE_EXPIRED", null);
        }

    }

    @Override
    public ResponseObject passwordResetting(String code, String password) {
        log.debug("TutorService ==> passwordResetting() - start:  code = {}, password = {}", code, password);
        var tutorOptional = userRepository.findByCode(code);
        if (tutorOptional.isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "CODE_NOT_EXIST", null);
        }
        long hours = getHours(tutorOptional);
        if (hours > 4) {
            log.debug("TutorService ==> sendEmailToPasswordReset() - end:  HttpStatus = {}, message = {}, token = {}", HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", null);
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", null);
        } else {
            var tutor = tutorOptional.get();
            tutor.setCode(null);
            tutor.setCodeTimeGenerated(null);
            tutor.setPassword(passwordEncoder.encode(password));
            userRepository.save(tutor);
            log.debug("TutorService ==> passwordResetting() - end:  HttpStatus = {}, message = {}, token = {}", HttpStatus.ACCEPTED, "PASSWORD_SUCCESSFULLY_CHANGED", null);
            return new ResponseObject(HttpStatus.ACCEPTED, "PASSWORD_SUCCESSFULLY_CHANGED", null);
        }
    }


    @Override
    public User getTutorByToken(HttpServletRequest request) {
        log.debug("TutorService ==> getTutorByToken() - start: HttpServletRequest = {}", request);
        var tutor = userRepository.findByEmail(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(request)));
        if (tutor.isEmpty()) {
            log.debug("TutorService ==> getTutorByToken() - end: AuthenticationException = {}", "Access denied");
            throw new AuthenticationException("ACCESS_DENIED");
        } else {
            log.debug("TutorService ==> getTutorByToken() - end: tutor = {}", tutor.get());
            return tutor.get();
        }

    }


    @Override
    @Transactional
    public ResponseObject resetPassword(User user, String password) {
        log.debug("TutorService ==> resetPassword() - start: email = {}", password);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        var response = new ResponseObject(HttpStatus.ACCEPTED, "PASSWORD_SUCCESSFULLY_UPDATED", refresh(user.getEmail()));
        log.debug("TutorService ==> resetPassword() - end: response = {}", response);
        return response;
    }


    public String refresh(String email) {
        log.debug("TutorService ==> refresh() - start: email = {}", email);
        var tutor = userRepository.findByEmail(email);
        if (tutor.isEmpty()) {
            log.debug("TutorService ==> refresh() - end: UserNotFoundException = {}", "USER_WITH_THIS_EMAIL_NOT_FOUND");
            throw new UserNotFoundException("USER_WITH_THIS_EMAIL_NOT_FOUND");
        } else {
            var token = jwtTokenProvider.createToken(email, Collections.singletonList(tutor.get().getAppUserRoles()));
            log.debug("TutorService ==> refresh() - start: token = {}", token);
            return token;
        }
    }


    private long getHours(Optional<User> optionalTutor) {
        Instant codeTimeGenerated = optionalTutor.get().getCodeTimeGenerated().toInstant();
        Instant now = Instant.now();

        Duration duration = Duration.between(codeTimeGenerated, now);
        long hours = duration.toHours();
        return hours;
    }
}
