package rideservice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rideservice.dto.LocationDTO;
import rideservice.dto.RideDTO;
import rideservice.dto.RideWithIdDTO;
import rideservice.entity.Ride;
import rideservice.mapper.RideWithIdMapper;
import rideservice.repository.RideRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final RideWithIdMapper rideWithIdMapper = RideWithIdMapper.INSTANCE;
    public RideService(RideRepository rideRepository)
    {
        this.rideRepository=rideRepository;
    }
    private Double calculatePrice(List<LocationDTO> locations) {
        return (double) locations.size() * 5.5;
    }
    @Transactional
    public RideWithIdDTO createRide(RideDTO rideDTO)
    {
        Ride ride= new Ride();
        ride.setCreatedAt(LocalDateTime.now());
        ride.setStatus("REQUESTED");
        ride.setPrice(calculatePrice(rideDTO.getLocations()));
        rideRepository.save(ride);
        return rideWithIdMapper.toDTO(ride);
    }
}
