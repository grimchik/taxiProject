package rideservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="locations")
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address",nullable = false)
    private String address;
    @Column(name = "latitude",nullable = false)
    private String  latitude;
    @Column(name = "longitude",nullable = false)
    private String longitude;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride;
}