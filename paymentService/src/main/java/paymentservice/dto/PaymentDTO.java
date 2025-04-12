package paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.AssertTrue;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO
{
    @NotNull(message = "Price cannot be null")
    @Positive(message = "Price must be a positive value")
    private Double price;

    @Pattern(regexp = "^(CARD|CASH)$", message = "Payment type must be either 'CARD' or 'CASH'")
    private String paymentType;

    private String cardNumber;

    @Hidden
    @JsonIgnore
    @AssertTrue(message = "Card number must be provided and in the correct format when payment type is CARD")
    public boolean isCardNumberValid() {
        if ("CASH".equalsIgnoreCase(paymentType)) {
            return cardNumber == null;
        }
        if ("CARD".equalsIgnoreCase(paymentType)) {
            return cardNumber != null && cardNumber.matches("^[0-9]{4}\\-[0-9]{4}\\-[0-9]{4}\\-[0-9]{4}$");
        }
        return true;
    }
    @NotNull(message = "rideId cannot be null")
    private Long rideId;
    @NotNull(message = "UserId cannot be null")
    private Long userId;
}
