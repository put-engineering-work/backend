package work.dto.user;

import work.domain.AppUserRole;
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
