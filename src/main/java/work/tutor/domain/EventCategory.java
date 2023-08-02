package work.tutor.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "event_category")
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
}