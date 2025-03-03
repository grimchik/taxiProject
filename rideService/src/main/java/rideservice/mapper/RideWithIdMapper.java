package rideservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import rideservice.dto.RideWithIdDTO;
import rideservice.entity.Ride;

@Mapper
public interface RideWithIdMapper  {
    RideWithIdMapper INSTANCE = Mappers.getMapper(RideWithIdMapper.class);
    @Mapping(source = "id",target = "id")
    Ride toEntity (RideWithIdDTO rideWithIdDTO);
    @Mapping(source = "id",target = "id")
    RideWithIdDTO toDTO (Ride ride);
}
