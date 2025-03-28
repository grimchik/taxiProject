package rideservice.unittest;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rideservice.dto.*;
import rideservice.entity.Ride;
import rideservice.entity.Location;
import rideservice.exception.ActiveRideException;
import rideservice.mapper.LocationMapper;
import rideservice.mapper.RideWithIdMapper;
import rideservice.repository.RideRepository;
import rideservice.service.RideService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private RideWithIdMapper rideWithIdMapper;

    @InjectMocks
    private RideService rideService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRide_Success() {
        RideDTO rideDTO = new RideDTO();
        rideDTO.setUserId(1L);
        rideDTO.setLocations(List.of(new LocationDTO()));

        Ride ride = new Ride();
        RideWithIdDTO rideWithIdDTO = new RideWithIdDTO();

        when(rideRepository.findByUserIdAndStatusIn(eq(rideDTO.getUserId()), anyList()))
                .thenReturn(Optional.empty());
        when(locationMapper.toEntity(any(LocationDTO.class))).thenReturn(new Location());
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);
        when(rideWithIdMapper.toDTO(ride)).thenReturn(rideWithIdDTO);

        RideWithIdDTO result = rideService.createRide(rideDTO);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    void testCreateRide_ActiveRideExists() {
        RideDTO rideDTO = new RideDTO();
        rideDTO.setUserId(1L);

        Ride activeRide = new Ride();
        activeRide.setStatus("IN_PROGRESS");

        when(rideRepository.findByUserIdAndStatusIn(eq(rideDTO.getUserId()), anyList()))
                .thenReturn(Optional.of(activeRide));

        assertThrows(ActiveRideException.class, () -> rideService.createRide(rideDTO));
    }

    @Test
    void testApplyRide_Success() {
        Long rideId = 1L;
        CarAndDriverIdDTO carAndDriverIdDTO = new CarAndDriverIdDTO(1L, 1L);

        Ride ride = new Ride();
        ride.setStatus("REQUESTED");
        RideWithIdDTO rideWithIdDTO = new RideWithIdDTO();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);
        when(rideWithIdMapper.toDTO(ride)).thenReturn(rideWithIdDTO);
        when(rideRepository.findByDriverIdAndStatusIn(eq(carAndDriverIdDTO.getDriverId()), anyList()))
                .thenReturn(Optional.empty());

        RideWithIdDTO result = rideService.applyRide(rideId, carAndDriverIdDTO);

        assertNotNull(result);
        verify(rideRepository, times(1)).save(ride);
    }

    @Test
    void testApplyRide_RideAlreadyAssigned() {
        Long rideId = 1L;
        CarAndDriverIdDTO carAndDriverIdDTO = new CarAndDriverIdDTO(1L, 1L);

        Ride ride = new Ride();
        ride.setDriverId(1L);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(IllegalStateException.class, () -> rideService.applyRide(rideId, carAndDriverIdDTO));
    }

    @Test
    void testCancelRide_Success() {
        CanceledRideDTO canceledRideDTO = new CanceledRideDTO();
        canceledRideDTO.setRideId(1L);
        canceledRideDTO.setUserId(1L);

        Ride ride = new Ride();
        ride.setStatus("REQUESTED");
        ride.setUserId(1L);

        when(rideRepository.findById(canceledRideDTO.getRideId())).thenReturn(Optional.of(ride));
        when(rideRepository.save(ride)).thenReturn(ride);

        rideService.cancelRide(canceledRideDTO);

        verify(rideRepository, times(1)).save(ride);
        assertEquals("CANCELED_BY_USER", ride.getStatus());
    }

    @Test
    void testCancelRide_NotAuthorized() {
        CanceledRideDTO canceledRideDTO = new CanceledRideDTO();
        canceledRideDTO.setRideId(1L);
        canceledRideDTO.setUserId(1L);

        Ride ride = new Ride();
        ride.setUserId(2L);

        when(rideRepository.findById(canceledRideDTO.getRideId())).thenReturn(Optional.of(ride));

        assertThrows(IllegalStateException.class, () -> rideService.cancelRide(canceledRideDTO));
    }

    @Test
    void testGetRideById_Success() {
        Long rideId = 1L;

        Ride ride = new Ride();
        RideWithIdDTO rideWithIdDTO = new RideWithIdDTO();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideWithIdMapper.toDTO(ride)).thenReturn(rideWithIdDTO);

        RideWithIdDTO result = rideService.getRideById(rideId);

        assertNotNull(result);
        verify(rideRepository, times(1)).findById(rideId);
    }

    @Test
    void testGetRideById_NotFound() {
        Long rideId = 1L;

        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> rideService.getRideById(rideId));
    }
}
