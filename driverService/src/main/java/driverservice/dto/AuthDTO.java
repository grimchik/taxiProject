package driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO
{
    @NotBlank(message = "username cannot be empty")
    private String username;
    @NotBlank(message = "password cannot be empty")
    private String password;
}
