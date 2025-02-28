package driverservice.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneDTO {
    @Pattern(
            regexp = "^\\+375\\d{9}$",
            message = "phone number must be in the format +375DDDDDDDDD"
    )
    private String phone;
}