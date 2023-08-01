package work.tutor.dto.tutor;

import work.tutor.domain.AppUserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TutorToken {

    private String token;

    private AppUserRole appUserRole;
}
