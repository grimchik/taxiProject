package clientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithoutPasswordDTO {
    @NotBlank(message = "username cannot be empty")
    private String username;
    @Pattern(
            regexp = "^\\+375\\s?(?:\\((24|29|25|33|44)\\)|24|29|25|33|44)\\s?\\d{3}-?\\d{2}-?\\d{2}$",
            message = "Phone number must be in the format +375(XX)XXX-XX-XX or +375XX XXX-XX-XX"
    )
    private String phone;
}