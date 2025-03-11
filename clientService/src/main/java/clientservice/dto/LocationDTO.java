package clientservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {

    @NotBlank(message = "Address cannot be null")
    private String address;

    @Pattern(regexp = "^(?!.*[a-zA-Z])[+-]?((1[0-7][0-9])|([1-9]?[0-9]))\\.[0-9]{1,6}$",
            message = "Invalid latitude, should be a number with optional decimal point and no letters")
    private String latitude;

    @Pattern(regexp = "^(?!.*[a-zA-Z])[+-]?((1[0-7][0-9])|([1-9]?[0-9]))\\.[0-9]{1,6}$",
            message = "Invalid longitude, should be a number with optional decimal point and no letters")
    private String longitude;

}