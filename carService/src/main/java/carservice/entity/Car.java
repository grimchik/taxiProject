package carservice.entity;

import carservice.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="brand",nullable = false)
    private String brand;
    @Column(name="model",nullable = false)
    private String model;
    @Column(name="description")
    private String description;
    @Column(name = "color",nullable = false)
    private String color;
    @Enumerated(EnumType.STRING)
    @Column(name ="category",nullable = false)
    private Category category;
    @Column(name="number",nullable = false,unique = true)
    private String number;
}


