package work.user.dto.user;

import work.user.domain.AppUserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserToken {

    private String token;

    private AppUserRole appUserRole;
}
