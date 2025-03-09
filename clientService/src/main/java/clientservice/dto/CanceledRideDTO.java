package clientservice.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CanceledRideDTO {
    private Long userId;
    private Long rideId;
}
