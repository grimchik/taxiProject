package rideservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RideDTO {
    @Size(min = 2, message = "There must be at least two locations")
    @Valid
    private List<LocationDTO> locations;
}
