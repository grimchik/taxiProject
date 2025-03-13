package clientservice.dto;

import clientservice.enums.Category;
import clientservice.enumvalidation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCarDTO {
    private String brand;
    private String model;
    private String description;
    private String color;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Pattern(
            regexp = "^[0-9]{4}[A-Z]{2}\\-[1-7]{1}$",
            message = "Number must be in format DDDDLL-D"
    )
    private String number;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ValueOfEnum(enumClass = Category.class)
    private String category;
}
