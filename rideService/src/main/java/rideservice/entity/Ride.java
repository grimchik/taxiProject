package rideservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="rides")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name ="status",nullable = false)
    private String status;
    private Double price;
    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
    private List<Location> locations;
    @Column(name ="created_at")
    private LocalDateTime createdAt;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "driver_id", nullable = true)
    private Long driverId;
    @Column(name = "car_id", nullable = true)
    private Long carId;
    @Column(name = "promo_code_applied", nullable = false)
    private Boolean promoCodeApplied = false;
}
