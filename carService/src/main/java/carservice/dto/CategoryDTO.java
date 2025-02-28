package carservice.dto;

import carservice.enums.Category;
import carservice.enumvalidation.ValueOfEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    @ValueOfEnum(enumClass = Category.class)
    private String category;
}
