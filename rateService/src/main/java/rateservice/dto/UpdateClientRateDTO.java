package rateservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClientRateDTO {
    @Min(value = 1, message = "Rate must be at least 1")
    @Max(value = 5, message = "Rate must be at most 5")
    private Long rate;

    private String comment;

    private Boolean cleanInterior;

    private Boolean safeDriving;

    private Boolean niceMusic;
}
