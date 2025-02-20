package driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class DriverWithIdDTO {
    private Long id;
    private String name;
    private String username;
    private String phone;
}
