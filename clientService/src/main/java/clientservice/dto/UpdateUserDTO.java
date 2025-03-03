package clientservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String password;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(
            regexp = "^\\+375\\d{9}$",
            message = "phone number must be in the format +375DDDDDDDDD"
    )
    private String phone;
    private String username;
}
