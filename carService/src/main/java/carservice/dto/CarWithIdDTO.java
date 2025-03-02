package carservice.dto;

import carservice.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarWithIdDTO {
    private Long id;
    private String brand;
    private String model;
    private String description;
    private String color;
    private String category;
    private String number;
}