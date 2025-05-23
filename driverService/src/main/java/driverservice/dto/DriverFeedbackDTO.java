package driverservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DriverFeedbackDTO {
    @NotNull(message = "Rate is required")
    @Min(value = 1, message = "Rate must be at least 1")
    @Max(value = 5, message = "Rate must be at most 5")
    private Long rate;

    private String comment;

    private Boolean politePassenger;

    private Boolean cleanPassenger;

    private Boolean punctuality;

    @NotNull(message = "Driver id cannot be null")
    private Long driverId;

    @NotNull(message = "Ride id cannot be null")
    private Long rideId;
}
