package clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideWithIdDTO {
    private Long userId;
    private Long id;
    private List<LocationDTO> locations;
    private String status;
    private Double price;
}
