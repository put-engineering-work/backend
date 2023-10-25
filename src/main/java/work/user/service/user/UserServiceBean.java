package work.user.service.user;

import work.user.domain.UserInfo;
import work.user.repository.UserDetailsRepository;
import work.user.repository.UserRepository;
import work.util.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@AllArgsConstructor
@Slf4j
@Service
public class UserServiceBean implements UserService {
    private final UserRepository userRepository;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    public UserInfo getUserDetails(Integer id) {
        return userDetailsRepository.getUserDetailsByUser(id).orElseThrow(UserNotFoundException::new);
    }
}
