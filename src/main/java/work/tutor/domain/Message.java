package work.tutor.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_date;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY)
    private List<MessageMember> messageMembers;
}