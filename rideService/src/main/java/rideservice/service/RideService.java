package rideservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rideservice.dto.LocationDTO;
import rideservice.dto.RideDTO;
import rideservice.dto.RideWithIdDTO;
import rideservice.dto.StatusDTO;
import rideservice.entity.Location;
import rideservice.entity.Ride;
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
        Ride ride = new Ride();
        ride.setCreatedAt(LocalDateTime.now());
        ride.setStatus("REQUESTED");
        ride.setPrice(calculatePrice(rideDTO.getLocations()));
        List<Location> locations = new ArrayList<>();
        for (LocationDTO locationDTO : rideDTO.getLocations()) {
            Location location = new Location();
            location.setAddress(locationDTO.getAddress());
            location.setLatitude(locationDTO.getLatitude());
            location.setLongitude(locationDTO.getLongitude());
            location.setRide(ride);
            locations.add(location);
        }
        ride.setLocations(locations);
        rideRepository.save(ride);

        return rideWithIdMapper.toDTO(ride);
    }

    @Transactional
    public String changeStatusById(Long id, StatusDTO statusDTO) {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isEmpty())
        {
            throw new EntityNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        ride.setStatus(statusDTO.getStatus());
        rideRepository.save(ride);
        return "Status changed successfully";
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

    @Transactional
    public RideWithIdDTO changeLocations(Long id, RideDTO rideDTO)
    {
        Optional<Ride> rideOptional = rideRepository.findById(id);
        if (rideOptional.isEmpty())
        {
            throw new EntityNotFoundException("Ride not found");
        }
        Ride ride = rideOptional.get();
        ride.setPrice(calculatePrice(rideDTO.getLocations()));

        List<Location> locations = new ArrayList<>();
        for (LocationDTO locationDTO : rideDTO.getLocations()) {
            Location location = new Location();
            location.setAddress(locationDTO.getAddress());
            location.setLatitude(locationDTO.getLatitude());
            location.setLongitude(locationDTO.getLongitude());
            location.setRide(ride);
            locations.add(location);
        }
        ride.setLocations(locations);
        rideRepository.save(ride);
        return rideWithIdMapper.toDTO(ride);
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
}
