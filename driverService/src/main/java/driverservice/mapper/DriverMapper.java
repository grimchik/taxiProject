package driverservice.mapper;

import driverservice.dto.DriverDTO;
import driverservice.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverMapper {
    DriverMapper INSTANCE = Mappers.getMapper(DriverMapper.class);
    @Mapping(target = "username", source = "username")
    Driver toEntity (DriverDTO driverDTO);
    @Mapping(target = "username", source = "username")
    DriverDTO toDTO (Driver driver);
}