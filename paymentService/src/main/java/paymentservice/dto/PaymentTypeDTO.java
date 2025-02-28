package paymentservice.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTypeDTO {
    @Pattern(regexp = "^(CARD|CASH)$", message = "Payment type must be either 'CARD' or 'CASH'")
    private String paymentType;

    private String cardNumber;

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
}
