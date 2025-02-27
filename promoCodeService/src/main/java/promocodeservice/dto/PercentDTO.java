package promocodeservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PercentDTO {
    @Min(value = 1, message = "Percent must be at least 1")
    @Max(value = 100, message = "Percent cannot be more than 100")
    private Long percent;
}
