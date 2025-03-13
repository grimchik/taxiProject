package paymentservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name="price",nullable = false)
    private Double price;
    @Column(name="payment_type",nullable = false)
    private String paymentType;
    @Column(name="payment_date",nullable = false)
    private LocalDateTime paymentDate;
    @Column(name="card_number",nullable = true)
    private String cardNumber;
    @Column(name="ride_id", nullable = false)
    private Long rideId;
    @Column(name="user_id", nullable = false)
    private Long userId;
}
