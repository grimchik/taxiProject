package rideservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreatePaymentDTO {
    @NotNull(message = "Price cannot be null")
    private Double price;
    @NotNull(message = "Ride Id cannot be null")
    private Long rideId;
    @NotNull(message = "User Id cannot be null")
    private Long userId;
}
