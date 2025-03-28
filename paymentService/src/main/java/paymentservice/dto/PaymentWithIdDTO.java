package paymentservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentWithIdDTO {
    private Long id;
    private Double price;
    private String paymentType;
    private LocalDateTime paymentDate;
    private String cardNumber;
    private Long rideId;
    private Long userId;
}
