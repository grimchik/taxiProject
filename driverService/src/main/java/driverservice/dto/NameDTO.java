package driverservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameDTO {
    @Pattern(
            regexp = "^[^0-9]*$",
            message = "the name field must not contain numbers"
    )
    private String name;
}
