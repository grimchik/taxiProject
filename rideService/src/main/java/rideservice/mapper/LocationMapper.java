package rideservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import rideservice.dto.LocationDTO;
import rideservice.entity.Location;

@Mapper
public interface LocationMapper {
    LocationMapper INSTANCE = Mappers.getMapper(LocationMapper.class);
    LocationDTO toDTO (Location location);
    Location toEntity (LocationDTO locationDTO);
}
