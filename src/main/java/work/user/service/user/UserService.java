package work.user.service.user;

import work.user.domain.User;
import work.user.dto.ResponseObject;
import work.user.dto.user.userdetails.GetUserDetailsDTO;

import javax.servlet.http.HttpServletRequest;

public interface UserService {


    ResponseObject createUser(User user);

    ResponseObject confirmRegistration(String code);

    ResponseObject signin(User user);

    ResponseObject sendEmailToPasswordReset(String email);

    ResponseObject checkCodeForPasswordResetting(String code);

    ResponseObject passwordResetting(String code, String password);

    ResponseObject resetPassword(User user, String password);

    User getTutorByToken(HttpServletRequest request);

    String refresh(String email);

    GetUserDetailsDTO getUserDetails(Integer userId);
}
