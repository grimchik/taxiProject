package driverservice.mapper;

import driverservice.dto.DriverDTO;
import driverservice.dto.DriverWithIdDTO;
import driverservice.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverWithIdMapper {
    DriverWithIdMapper INSTANCE = Mappers.getMapper(DriverWithIdMapper.class);

    @Mapping(target = "id", source = "driverWithIdDTO.id")
    Driver toEntity(DriverWithIdDTO driverWithIdDTO);

    @Mapping(target = "id", source = "driver.id")
    DriverWithIdDTO toDTO(Driver driver);
}

