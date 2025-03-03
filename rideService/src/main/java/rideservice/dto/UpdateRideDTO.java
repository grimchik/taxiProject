package rideservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rideservice.enums.Status;
import rideservice.enumvalidation.ValueOfEnum;

import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRideDTO {
    @Size(min = 2, message = "There must be at least two locations")
    @Valid
    private List<LocationDTO> locations;

    @ValueOfEnum(enumClass = Status.class)
    private String status;
}
