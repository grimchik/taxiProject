package driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsernameDTO {
    @NotBlank(message = "Username cannot be empty")
    private String username;
}