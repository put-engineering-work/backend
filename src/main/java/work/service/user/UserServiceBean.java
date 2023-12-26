package work.service.user;

import org.springframework.web.multipart.MultipartFile;
import work.domain.AppUserRole;
import work.domain.User;
import work.dto.ResponseObject;
import work.dto.user.UserToken;
import work.dto.user.userdetails.GetUserDetailsDTO;
import work.dto.user.userdetails.UpdateUserDetailsDTO;
import work.repository.UserDetailsRepository;
import work.repository.UserRepository;
import work.service.authentication.AuthenticationService;
import work.service.email.EmailDetails;
import work.service.email.EmailService;
import work.service.imageoperation.ImageOperationService;
import work.service.util.UtilService;
import work.util.exception.CustomException;
import work.util.exception.UserNotFoundException;
import work.util.mapstruct.UserMapper;
import work.util.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserDetailsRepository userDetailsRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final UtilService utilService;


    @Override
    public ResponseObject createUser(User user) {
        var userFromDb = userRepository.findByEmail(user.getEmail());

        if (userFromDb.isEmpty()) {
            user.setIsActivated(Boolean.FALSE);
            String code = AuthenticationService.generateRandomAlphanumeric(30);
            user.setCode(code);
            user.setAppUserRoles(AppUserRole.ROLE_USER);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setCodeTimeGenerated(ZonedDateTime.now());
            var userDetails = user.getUserDetails();
            user.setUserDetails(null);
            var savedUser = userRepository.saveAndFlush(user);
            var savedUserDetails = userDetailsRepository.saveAndFlush(userDetails);
            userRepository.setUserDetailsId(savedUser.getId(), savedUserDetails.getId());
            userDetailsRepository.setUserDetailsId(savedUser.getId(), savedUserDetails.getId());
            emailService.emailConfirmation(new EmailDetails(user.getEmail(),
                    "Welcome to LeisureLink app " + savedUserDetails.getName()
                            + " "
                            + savedUserDetails.getLastName(), "Email confirmation"));
            return new ResponseObject(HttpStatus.ACCEPTED, "USER_CREATED", null);
        } else {
            throw new CustomException(HttpStatus.UNPROCESSABLE_ENTITY, "USER_ALREADY_EXIST");
        }
    }

    @Override
    public ResponseObject confirmRegistration(String code) {
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
                return new ResponseObject(HttpStatus.OK, "REGISTRATION_CONFIRMED", token);
            } else {

                return new ResponseObject(HttpStatus.OK, "VERIFICATION_LINK_WAS_SEND_AGAIN", null);
            }
        } else {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "INVALID_REGISTRATION_CODE", null);
        }
    }


    @Override
    public ResponseObject signin(User user) {
        UserToken userToken;
        Optional<User> userLoginData = userRepository.findByEmail(user.getEmail());
        if (userLoginData.isEmpty()) {
            throw new UserNotFoundException("USER_NOT_FOUND");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            String token = jwtTokenProvider.createToken(
                    userLoginData.get().getEmail(),
                    new LinkedList<>(Collections.singletonList(userLoginData.get().getAppUserRoles())));
            userToken = new UserToken();
            userToken.setToken(token);
            userToken.setAppUserRole(userLoginData.get().getAppUserRoles());
            return new ResponseObject(HttpStatus.ACCEPTED, "CORRECT_LOGIN_DATA", token, userLoginData.get().getAppUserRoles());
        } catch (Exception e) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "WRONG_DATA", null);
        }

    }

    @Override
    public ResponseObject sendEmailToPasswordReset(String email) {
        var tutor = userRepository.findByEmail(email);
        if (tutor.isEmpty()) {
            return new ResponseObject(HttpStatus.BAD_REQUEST, "WRONG_DATA", null);
        } else if (!tutor.get().getIsActivated()) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "ACCOUNT_IS_NOT_ACTIVATED", null);
        } else {
            String code = AuthenticationService.generateRandomAlphanumeric(30);
            tutor.get().setCode(code);
            tutor.get().setCodeTimeGenerated(ZonedDateTime.now());
            userRepository.save(tutor.get());

            return new ResponseObject(HttpStatus.ACCEPTED, "EMAIL_SENT", null);
        }
    }

    @Override
    public ResponseObject checkCodeForPasswordResetting(String code) {
        var tutor = userRepository.findByCode(code);
        if (tutor.isEmpty()) {
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "INVALID_CODE", null);
        } else if (!tutor.get().getIsActivated()) {
            return new ResponseObject(HttpStatus.UNPROCESSABLE_ENTITY, "ACCOUNT_IS_NOT_ACTIVATED", null);
        }
        long hours = getHours(tutor);
        if (hours < 4) {
            String newCode = AuthenticationService.generateRandomAlphanumeric(30);
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
            return new ResponseObject(HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED", null);
        } else {
            var tutor = tutorOptional.get();
            tutor.setCode(null);
            tutor.setCodeTimeGenerated(null);
            tutor.setPassword(passwordEncoder.encode(password));
            userRepository.save(tutor);
            return new ResponseObject(HttpStatus.ACCEPTED, "PASSWORD_SUCCESSFULLY_CHANGED", null);
        }
    }

    @Override
    @Transactional
    public ResponseObject resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return new ResponseObject(HttpStatus.ACCEPTED, "PASSWORD_SUCCESSFULLY_UPDATED", refresh(user.getEmail()));
    }

    @Override
    public GetUserDetailsDTO getUserDetails(UUID userId) {
        var userDetails = userDetailsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));
        var response=userMapper.getUserDetailsData(userDetails);
        response.setPhoto(utilService.decompressImage(response.getPhoto()));
        return response;
    }

    @Override
    public ResponseObject updateUserDetails(UpdateUserDetailsDTO updateUserDetailsDTO, UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));
        var userDetails = userMapper.fromUpdateUserDetails(updateUserDetailsDTO);
        if (user.getUserDetails() != null)
            userDetails.setId(user.getUserDetails().getId());
        userDetails.setUser(user);
        userDetails = userDetailsRepository.saveAndFlush(userDetails);
        user.setUserDetails(userDetails);
        userRepository.save(user);
        return new ResponseObject(HttpStatus.ACCEPTED, "DATA_SUCCESSFULLY_UPDATED", null);
    }

    @Transactional
    public ResponseObject updateUserImage(UUID userId, MultipartFile photo){
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));
        if (user.getUserDetails()!=null){
            user.getUserDetails().setPhoto(utilService.compressImage(photo, 0.5f));
            userRepository.save(user);
        }
        return new ResponseObject(HttpStatus.OK, "DATA_SUCCESSFULLY_UPDATED", null);


    }

    public String refresh(String email) {
        var tutor = userRepository.findByEmail(email);
        if (tutor.isEmpty()) {
            throw new UserNotFoundException("USER_WITH_THIS_EMAIL_NOT_FOUND");
        } else {
            return jwtTokenProvider.createToken(email, Collections.singletonList(tutor.get().getAppUserRoles()));
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
