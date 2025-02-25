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
            regexp = "^\\+375[0-9]{9}$",
            message = "phone number must be in the format +375DDDDDDDDD"
    )
    private String phone;
}