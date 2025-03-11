package clientservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeDTO {

    @Min(value = 1, message = "Percent must be at least 1")
    @Max(value = 100, message = "Percent cannot be more than 100")
    private Long percent;

    @NotNull(message = "Activation date must not be null")
    private LocalDate activationDate;

    @NotBlank(message = "Keyword must not be blank")
    private String keyword;

    @NotNull(message = "Expiry date must not be null")
    private LocalDate expiryDate;
}
