package driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    @Pattern(
            regexp = "^[^0-9]*$",
            message = "the name field must not contain numbers"
    )
    private String name;
    @NotBlank(message = "username cannot be empty")
    private String username;
    @NotBlank(message = "password cannot be empty")
    private String password;
    @Pattern(
            regexp = "^\\+375\\d{9}$",
            message = "phone number must be in the format +375DDDDDDDDD"
    )
    private String phone;
}