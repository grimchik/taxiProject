package clientservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetRidesRequestDTO {
    @NotBlank(message = "User Id cannot be null")
    private Long userId;
    @NotNull(message = "Page cannot be null")
    private int page;
    @NotNull(message = "Size cannot be null")
    @Min(value = 1, message = "Size must be at least 1")
    private int size;
}
