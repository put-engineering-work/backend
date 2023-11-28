package work.domain;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "events")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String name;

    private String address;

    private String description;

    private ZonedDateTime startDate;

    private ZonedDateTime endDate;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event")
    private Set<Member> members = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<EventImage> eventImages = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<EventCategory> categories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();
}