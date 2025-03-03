package driverservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDriverDTO {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(
            regexp = "^[^0-9]*$",
            message = "the name field must not contain numbers"
    )
    private String name;
    private String username;
    private String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(
            regexp = "^\\+375\\s?(?:\\((24|29|25|33|44)\\)|24|29|25|33|44)\\s?\\d{3}-?\\d{2}-?\\d{2}$",
            message = "Phone number must be in the format +375(XX)XXX-XX-XX or +375XX XXX-XX-XX"
    )
    private String phone;
}
