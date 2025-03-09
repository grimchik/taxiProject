package clientservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRideRequestDTO {
    @NotBlank(message = "user id cannot be null")
    private Long userId;
    @Size(min = 2, message = "There must be at least two locations")
    @Valid
    private List<LocationDTO> locations;
}
