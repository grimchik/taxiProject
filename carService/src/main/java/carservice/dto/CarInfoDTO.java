package carservice.dto;

import carservice.enums.Category;
import carservice.enumvalidation.ValueOfEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
public class CarInfoDTO {
    @NotBlank(message = "brand cannot be empty")
    private String brand;
    @NotBlank(message = "model cannot be empty")
    private String model;
    private String description;
    @NotBlank(message = "color cannot be empty")
    private String color;
}