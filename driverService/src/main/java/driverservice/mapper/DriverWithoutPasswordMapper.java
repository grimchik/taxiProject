package driverservice.mapper;

import driverservice.dto.DriverWithoutPasswordDTO;
import driverservice.entity.Driver;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DriverWithoutPasswordMapper {
    DriverWithoutPasswordMapper INSTANCE = Mappers.getMapper(DriverWithoutPasswordMapper.class);
    @Mapping(target = "username",source = "username")
    DriverWithoutPasswordDTO toDTO (Driver driver);
    @Mapping(target = "username",source = "username")
    Driver toEntity(DriverWithoutPasswordDTO driverWithoutPasswordDTO);
}
