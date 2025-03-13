package driverservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinishRideDTO {
    @NotNull(message = "Driver id cannot be null")
    private Long driverId;
    @NotNull(message = "Ride id cannot be null")
    private Long rideId;
}
