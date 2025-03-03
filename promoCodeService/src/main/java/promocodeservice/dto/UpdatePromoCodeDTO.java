package promocodeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePromoCodeDTO {
    private LocalDate activationDate;
    private LocalDate expiryDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Min(value = 1, message = "Percent must be at least 1")
    @Max(value = 100, message = "Percent cannot be more than 100")
    private Long percent;

    @Pattern(regexp = "^(?!\\s*$).+", message = "Keyword must not be blank")
    private String keyword;
}
