package rideservice.dto;

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
public class RideDTO {
    private Long userId;
    @Size(min = 2, message = "There must be at least two locations")
    @Valid
    private List<LocationDTO> locations;
}
