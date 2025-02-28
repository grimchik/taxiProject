package promocodeservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ActivationDateDTO {
    @NotNull(message = "Activation date must not be null")
    private LocalDate activationDate;
}
