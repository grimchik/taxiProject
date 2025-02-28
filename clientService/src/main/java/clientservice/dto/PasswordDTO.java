package clientservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PasswordDTO {
    @NotBlank(message = "Password cannot be empty")
    private String password;
}