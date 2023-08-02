package work.user.domain;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "message")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Event event;

    private String message;

    private ZonedDateTime createdDate;

    @ManyToMany(mappedBy = "messages", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Member> members = new HashSet<>();

}