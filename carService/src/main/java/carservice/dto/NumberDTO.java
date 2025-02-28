package carservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberDTO {
    @Pattern(
            regexp = "^[0-9]{4}[A-Z]{2}\\-[1-7]{1}$",
            message = "Number must be in format DDDDLL-D")
    private String number;
}