package rideservice.service;

import jakarta.persistence.EntityNotFoundException;
import liquibase.command.core.UpdateCountSqlCommandStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rideservice.dto.LocationDTO;
import rideservice.dto.RideDTO;
import rideservice.dto.RideWithIdDTO;
import rideservice.dto.UpdateRideDTO;
import rideservice.entity.Location;
import rideservice.entity.Ride;
import rideservice.mapper.LocationMapper;
import rideservice.mapper.RideWithIdMapper;
import rideservice.repository.RideRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RideService {
    private final RideRepository rideRepository;
    private final RideWithIdMapper rideWithIdMapper = RideWithIdMapper.INSTANCE;
    private final LocationMapper locationMapper = LocationMapper.INSTANCE;
    public RideService(RideRepository rideRepository)
    {
        this.rideRepository=rideRepository;
    }
    private Double calculatePrice(List<?> locations) {
        return (double) locations.size() * 5.5;
    }

    @Transactional
    public RideWithIdDTO createRide(RideDTO rideDTO)
    {
        Ride ride = new Ride();
        ride.setCreatedAt(LocalDateTime.now());
        ride.setStatus("REQUESTED");
        ride.setPrice(calculatePrice(rideDTO.getLocations()));
        List<Location> locations = new ArrayList<>();
        for (LocationDTO locationDTO : rideDTO.getLocations()) {
            Location location = locationMapper.toEntity(locationDTO);
            location.setRide(ride);
            locations.add(location);
        }
        ride.setLocations(locations);
        rideRepository.save(ride);

        return rideWithIdMapper.toDTO(ride);
    }

    @Transactional
    public RideWithIdDTO changeRide(Long id, UpdateRideDTO updateRideDTO) {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isEmpty())
        {
            throw new EntityNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        if (updateRideDTO.getLocations() != null)
        {
            for (LocationDTO locationDTO : updateRideDTO.getLocations()) {
                Location location = locationMapper.toEntity(locationDTO);
                location.setRide(ride);
                ride.getLocations().add(location);
            }
        }
        if (updateRideDTO.getStatus() != null) {
            ride.setStatus(updateRideDTO.getStatus());
        }
        rideRepository.save(ride);
        return rideWithIdMapper.toDTO(ride);
    }

    @Transactional
    public void deleteRideById(Long id)
    {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isEmpty())
        {
            throw new EntityNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        rideRepository.delete(ride);
    }

    public RideWithIdDTO getRideById(Long id)
    {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isEmpty())
        {
            throw new EntityNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        return rideWithIdMapper.toDTO(ride);
    }

    public Page<RideWithIdDTO> getAllRides(Pageable pageable) {
        return rideRepository.findAll(pageable).map(rideWithIdMapper::toDTO);
    }
}
