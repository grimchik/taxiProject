package rateservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
@Table(name="driver_feedbacks")
public class DriverFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comment",nullable = true)
    private String comment;

    @Column(name ="rate",nullable = false)
    private Long rate;

    @Column(nullable = true)
    private Boolean politePassenger;

    @Column(nullable = true)
    private Boolean cleanPassenger;

    @Column(nullable = true)
    private Boolean punctuality;
}
