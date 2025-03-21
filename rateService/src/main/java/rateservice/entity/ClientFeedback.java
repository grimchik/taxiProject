package rateservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Table(name="client_feedbacks")
public class ClientFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rate",nullable = false)
    private Long rate;

    @Column(nullable = true)
    private String comment;

    @Column(nullable = true)
    private Boolean cleanInterior;

    @Column(nullable = true)
    private Boolean safeDriving;

    @Column(nullable = false)
    private Boolean niceMusic;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ride_id", nullable = false)
    private Long rideId;
}
