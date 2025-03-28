package driverservice.unittest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import driverservice.dto.*;
import driverservice.entity.Driver;
import driverservice.mapper.DriverMapper;
import driverservice.mapper.DriverWithIdMapper;
import driverservice.mapper.DriverWithoutPasswordMapper;
import driverservice.repository.DriverRepository;
import driverservice.service.DriverService;
import driverservice.client.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private DriverMapper driverMapper;

    @Mock
    private RideServiceClient rideServiceClient;

    @Mock
    private DriverWithIdMapper driverWithIdMapper;

    @Mock
    private DriverWithoutPasswordMapper driverWithoutPasswordMapper;

    @Mock
    private DriverFeedbackServiceClient driverFeedbackServiceClient;

    @Mock
    private CarServiceClient carServiceClient;

    @InjectMocks
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateDriver_Success() {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg");
        driverDTO.setPhone("+375(29)123-45-67");
        driverDTO.setPassword("password123");

        Driver driver = new Driver();
        driver.setUsername("Oleg");

        DriverWithIdDTO driverWithIdDTO = new DriverWithIdDTO();

        when(driverRepository.findByUsername(driverDTO.getUsername())).thenReturn(Optional.empty());
        when(driverRepository.findByPhone(driverDTO.getPhone())).thenReturn(Optional.empty());
        when(driverMapper.toEntity(driverDTO)).thenReturn(driver);
        when(passwordEncoder.encode(driverDTO.getPassword())).thenReturn("encryptedPassword123");
        when(driverWithIdMapper.toDTO(driver)).thenReturn(driverWithIdDTO);

        DriverWithIdDTO result = driverService.createDriver(driverDTO);

        assertNotNull(result);

        ArgumentCaptor<Driver> captor = ArgumentCaptor.forClass(Driver.class);
        verify(driverRepository, times(1)).save(captor.capture());

        Driver savedDriver = captor.getValue();
        assertEquals("Oleg", savedDriver.getUsername());
        assertEquals("+375(29)123-45-67", savedDriver.getPhone());
        assertEquals("encryptedPassword123", savedDriver.getPassword());
    }



    @Test
    void testCreateDriver_DuplicateUsername() {
        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("Oleg");

        when(driverRepository.findByUsername(driverDTO.getUsername())).thenReturn(Optional.of(new Driver()));

        assertThrows(EntityExistsException.class, () -> driverService.createDriver(driverDTO));
    }

    @Test
    void testDeleteById_Success() {
        Long driverId = 1L;

        Driver driver = new Driver();
        driver.setUsername("Oleg");
        driver.setIsDeleted(false);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));

        driverService.deleteById(driverId);

        verify(driverRepository, times(1)).softDeleteByUsername(driver.getUsername());
    }

    @Test
    void testDeleteById_AlreadyDeleted() {
        Long driverId = 1L;

        Driver driver = new Driver();
        driver.setIsDeleted(true);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));

        assertThrows(IllegalStateException.class, () -> driverService.deleteById(driverId));
    }

    @Test
    void testAssignCarToDriver_Success() {
        Long driverId = 1L;
        Long carId = 2L;

        Driver driver = new Driver();
        driver.setIsDeleted(false);

        CarWithIdDTO car = new CarWithIdDTO();
        car.setId(carId);
        car.setCategory("ECONOMY");

        RateDTO rateDTO = new RateDTO(5.0);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverRepository.findDriverByCarId(carId)).thenReturn(Optional.empty());
        when(carServiceClient.getCarById(carId)).thenReturn(car);
        when(driverFeedbackServiceClient.getDriverRate(driverId)).thenReturn(rateDTO);
        when(rideServiceClient.getActiveRide(driverId)).thenReturn(new RideWithIdDTO());

        DriverWithIdDTO driverWithIdDTO = new DriverWithIdDTO();
        when(driverWithIdMapper.toDTO(driver)).thenReturn(driverWithIdDTO);

        DriverWithIdDTO result = driverService.assignCarToDriver(driverId, carId);

        assertNotNull(result);
        verify(driverRepository, times(1)).save(driver);
        assertEquals(carId, driver.getCarId());
    }


    @Test
    void testUpdateProfile_Success() {
        Long driverId = 1L;

        Driver driver = new Driver();
        driver.setUsername("old_username");
        driver.setPhone("+375335678951");

        DriverDTO driverDTO = new DriverDTO();
        driverDTO.setUsername("new_username");
        driverDTO.setPhone("+375335678950");
        driverDTO.setPassword("new_password");

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(passwordEncoder.encode(driverDTO.getPassword())).thenReturn("encoded_password");
        when(driverWithoutPasswordMapper.toDTO(driver)).thenReturn(new DriverWithoutPasswordDTO());

        DriverWithoutPasswordDTO result = driverService.updateProfile(driverId, driverDTO);

        assertNotNull(result);
        verify(driverRepository, times(1)).save(driver);
        assertEquals("new_username", driver.getUsername());
        assertEquals("+375335678950", driver.getPhone());
    }

    @Test
    void testGetDriverById_Success() {
        Long driverId = 1L;
        Driver driver = new Driver();
        DriverWithIdDTO driverWithIdDTO = new DriverWithIdDTO();

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));
        when(driverWithIdMapper.toDTO(driver)).thenReturn(driverWithIdDTO);

        DriverWithIdDTO result = driverService.getDriverById(driverId);

        assertNotNull(result);
        verify(driverRepository, times(1)).findById(driverId);
    }

    @Test
    void testGetDriverById_NotFound() {
        Long driverId = 1L;

        when(driverRepository.findById(driverId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> driverService.getDriverById(driverId));
    }
}