package carservice.mapper;

import carservice.dto.CarWithIdDTO;
import carservice.entity.Car;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CarWithIdMapper {
    CarWithIdMapper INSTANCE = Mappers.getMapper(CarWithIdMapper.class);
    @Mapping(target = "number",source = "number")
    Car toEntity(CarWithIdDTO carWithIdDTO);
    @Mapping(target = "number",source = "number")
    CarWithIdDTO toDTO (Car car);
}
