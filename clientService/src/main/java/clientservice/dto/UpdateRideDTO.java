package clientservice.dto;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRideDTO {
    private Long userId;
    @Size(min = 2, message = "There must be at least two locations")
    @Valid
    private List<LocationDTO> locations;
}
