package work.service.user;

import work.domain.User;
import work.dto.ResponseObject;
import work.dto.user.userdetails.GetUserDetailsDTO;
import work.dto.user.userdetails.UpdateUserDetailsDTO;

import javax.servlet.http.HttpServletRequest;

public interface UserService {


    ResponseObject createUser(User user);

    ResponseObject confirmRegistration(String code);

    ResponseObject signin(User user);

    ResponseObject sendEmailToPasswordReset(String email);

    ResponseObject checkCodeForPasswordResetting(String code);

    ResponseObject passwordResetting(String code, String password);

    ResponseObject resetPassword(User user, String password);

    User getUserByToken(HttpServletRequest request);

    String refresh(String email);

    GetUserDetailsDTO getUserDetails(Integer userId);

    ResponseObject updateUserDetails(UpdateUserDetailsDTO updateUserDetailsDTO, Integer userId);
}
