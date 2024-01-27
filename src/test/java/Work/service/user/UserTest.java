package Work.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import work.domain.AppUserRole;
import work.domain.User;
import work.domain.UserDetails;
import work.dto.ResponseObject;
import work.repository.UserRepository;
import work.repository.UserDetailsRepository;
import work.service.email.EmailService;
import work.service.user.UserServiceBean;
import work.util.exception.UserNotFoundException;
import work.util.security.JwtTokenProvider;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private EmailService emailService;


    @InjectMocks
    private UserServiceBean userService;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser_UserDoesNotExist_UserCreated() {
        User newUser = new User();
        newUser.setEmail("test@example.com");
        newUser.setPassword("password123");
        newUser.setAppUserRoles(AppUserRole.ROLE_USER);
        newUser.setUserDetails(new UserDetails());

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");

        UserDetails savedUserDetails = new UserDetails();
        savedUserDetails.setId(UUID.randomUUID());
        when(userDetailsRepository.saveAndFlush(any(UserDetails.class))).thenReturn(savedUserDetails);


        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        ResponseObject responseObject = userService.createUser(newUser);

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).saveAndFlush(any(User.class));
        assertNotNull(responseObject);
        assertEquals("USER_CREATED", responseObject.getMessage());
    }

    @Test
    public void confirmRegistration_ValidCode_UserActivated() {
        String code = "validCode";
        User user = new User();
        user.setCodeTimeGenerated(ZonedDateTime.now().minusHours(1));
        user.setIsActivated(false);

        when(userRepository.findByCode(code)).thenReturn(Optional.of(user));

        ResponseObject responseObject = userService.confirmRegistration(code);

        assertTrue(user.getIsActivated());
        assertNull(user.getCode());
        assertNull(user.getCodeTimeGenerated());
        assertEquals(HttpStatus.OK, responseObject.getCode());
        assertEquals("REGISTRATION_CONFIRMED", responseObject.getMessage());
    }

    @Test
    public void signin_ValidCredentials_ReturnsToken() {
        User user = new User();
        user.setEmail("user123@example.com");
        user.setPassword("password123");
        user.setAppUserRoles(AppUserRole.ROLE_USER);
        user.setUserDetails(new UserDetails());


        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("password123");

        UserDetails savedUserDetails = new UserDetails();
        savedUserDetails.setId(UUID.randomUUID());
        when(userDetailsRepository.saveAndFlush(any(UserDetails.class))).thenReturn(savedUserDetails);


        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);

        userService.createUser(user);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.createToken(anyString(), anyList())).thenReturn("token");

        ResponseObject responseObject = new ResponseObject(HttpStatus.ACCEPTED, "CORRECT_LOGIN_DATA", "123", null);

        assertEquals(HttpStatus.ACCEPTED, responseObject.getCode());
        assertEquals("CORRECT_LOGIN_DATA", responseObject.getMessage());
    }

    @Test
    public void signin_InvalidCredentials_ReturnsToken() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password123");
        user.setAppUserRoles(AppUserRole.ROLE_USER);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        ResponseObject responseObject = userService.signin(user);

        assertEquals(HttpStatus.UNAUTHORIZED, responseObject.getCode());
        assertEquals("WRONG_DATA", responseObject.getMessage());

    }

    @Test
    public void resetPassword_ValidCodeAndPassword_PasswordReset() {
        String code = "validCode";
        String newPassword = "newPassword";
        User user = new User();
        user.setCode(code);
        user.setCodeTimeGenerated(ZonedDateTime.now().minusHours(1));

        when(userRepository.findByCode(code)).thenReturn(Optional.of(user));

        ResponseObject responseObject = userService.passwordResetting(code, newPassword);

        verify(passwordEncoder).encode(newPassword);
        assertNull(user.getCode());
        assertNull(user.getCodeTimeGenerated());
        assertEquals(HttpStatus.ACCEPTED, responseObject.getCode());
        assertEquals("PASSWORD_SUCCESSFULLY_CHANGED", responseObject.getMessage());
    }
}