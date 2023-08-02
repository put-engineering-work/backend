package work.user.domain;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String address;

    private String description;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Member> members = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<EventImage> eventImages = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventCategory> categories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();
}