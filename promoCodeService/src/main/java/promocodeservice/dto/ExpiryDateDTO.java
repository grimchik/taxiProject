package promocodeservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExpiryDateDTO {
    @NotNull(message = "Expiry date must not be null")
    private LocalDate expiryDate;
}
