package rideservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarAndDriverIdDTO {
    @NotNull(message = "Car id cannot be null")
    private Long carId;
    @NotNull(message = "Driver id cannot be null")
    private Long driverId;
}
