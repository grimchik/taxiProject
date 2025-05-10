package clientservice.dto;

import clientservice.enums.Category;
import clientservice.enumvalidation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDTO {
    @NotBlank(message = "brand cannot be empty")
    private String brand;
    @NotBlank(message = "model cannot be empty")
    private String model;
    private String description;
    @NotBlank(message = "color cannot be empty")
    private String color;
    @ValueOfEnum(enumClass = Category.class)
    private String category;
    @Pattern(
            regexp = "^[0-9]{4}[A-Z]{2}\\-[1-7]{1}$",
            message = "Number must be in format DDDDLL-D")
    private String number;
}