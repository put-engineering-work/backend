package work.tutor.domain;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "event")
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

    @Temporal(TemporalType.TIMESTAMP)
    private Date start_date;

    @Temporal(TemporalType.TIMESTAMP)
    private Date end_date;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventMember> eventMembers;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventImage> eventImages;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_category",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<EventCategory> categories;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Comment> comments;
}