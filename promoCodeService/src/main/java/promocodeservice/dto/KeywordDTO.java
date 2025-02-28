package promocodeservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordDTO {
    @NotBlank(message = "Keyword must not be blank")
    private String keyword;
}
