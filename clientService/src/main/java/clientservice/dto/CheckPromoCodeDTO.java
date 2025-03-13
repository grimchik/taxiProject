package clientservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckPromoCodeDTO {
    @NotNull(message = "User id cannot be null")
    private Long userId;
    @Pattern(regexp = "^(?!\\s*$).+", message = "Keyword must not be blank")
    private String keyword;
}
