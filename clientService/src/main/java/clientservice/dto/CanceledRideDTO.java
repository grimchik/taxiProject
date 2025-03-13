package clientservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CanceledRideDTO {
    @NotNull(message = "User id cannot be null")
    private Long userId;
    @NotNull(message = "Ride id cannot be null")
    private Long rideId;
}
