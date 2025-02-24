package carservice.mapper;

import carservice.dto.CarDTO;
import carservice.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarMapper {
    CarMapper INSTANCE = Mappers.getMapper(CarMapper.class);
    @Mapping(target = "number",source = "number")
    Car toEntity (CarDTO carDTO);
    @Mapping(target = "number",source = "number")
    CarDTO toDTO(Car car);
}