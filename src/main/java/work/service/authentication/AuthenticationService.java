package work.service.authentication;

import work.domain.User;

import javax.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    User getUserByToken(HttpServletRequest request);
}
