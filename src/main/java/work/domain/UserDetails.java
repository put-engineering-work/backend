package work.domain;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_details")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String lastName;

    private String address;

    private String phoneNumber;

    private ZonedDateTime birthDate;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(nullable = false)
    private User user;
}



