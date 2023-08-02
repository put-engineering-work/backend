package work.user.domain;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    private Boolean isActivated = Boolean.FALSE;

    private AppUserRole appUserRoles;

    private String code;

    private ZonedDateTime codeTimeGenerated;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserDetails userDetails;
}

