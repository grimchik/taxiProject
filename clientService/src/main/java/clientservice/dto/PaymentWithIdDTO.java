package clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
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
}
