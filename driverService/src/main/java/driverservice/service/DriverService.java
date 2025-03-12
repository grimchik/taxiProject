package driverservice.service;

import driverservice.client.CarServiceClient;
import driverservice.client.DriverFeedbackServiceClient;
import driverservice.client.RideServiceClient;
import driverservice.dto.*;
import driverservice.entity.Driver;
import driverservice.enums.Category;
import driverservice.exception.SamePasswordException;
import driverservice.mapper.DriverMapper;
import driverservice.mapper.DriverWithIdMapper;
import driverservice.mapper.DriverWithoutPasswordMapper;
import driverservice.repository.DriverRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverMapper driverMapper = DriverMapper.INSTANCE;
    private final DriverWithoutPasswordMapper driverWithoutPasswordMapper = DriverWithoutPasswordMapper.INSTANCE;
    private final DriverWithIdMapper driverWithIdMapper = DriverWithIdMapper.INSTANCE;

    private final RideServiceClient rideServiceClient;
    private final DriverFeedbackServiceClient driverFeedbackServiceClient;
    private final CarServiceClient carServiceClient;
    public DriverService(DriverRepository driverRepository, PasswordEncoder passwordEncoder,
                         RideServiceClient rideServiceClient, DriverFeedbackServiceClient driverFeedbackServiceClient,
                         CarServiceClient carServiceClient) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.rideServiceClient = rideServiceClient;
        this.driverFeedbackServiceClient=driverFeedbackServiceClient;
        this.carServiceClient=carServiceClient;
    }

    public DriverWithIdDTO createDriver(DriverDTO driverDTO) {
        if (driverRepository.findByUsername(driverDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
        if (driverRepository.findByPhone(driverDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
        Driver driver = driverMapper.toEntity(driverDTO);
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

    @Transactional
    public void deleteById(Long id) {
        Driver driver = findActiveDriverById(id);
        driverRepository.softDeleteByUsername(driver.getUsername());
    }

    private Driver findActiveDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        checkIsDeleted(driver.getIsDeleted());
        return driver;
    }

    private void checkIsDeleted(boolean deleted) {
        if (deleted) {
            throw new IllegalStateException("Driver has already been deleted");
        }
    }

    public DriverWithIdDTO getDriverById(Long id) {
        Driver driver = findActiveDriverById(id);
        return driverWithIdMapper.toDTO(driver);
    }

    public Page<DriverWithIdDTO> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverWithIdMapper::toDTO);
    }

    @Transactional
    public DriverWithoutPasswordDTO updateProfile(Long id, DriverDTO driverDTO) {
        Driver driver = findActiveDriverById(id);
        checkUsername(driver, driverDTO.getUsername());
        checkPhone(driver, driverDTO.getPhone());
        checkPassword(driver, driverDTO.getPassword());
        driver.setName(driverDTO.getName());
        driver.setUsername(driverDTO.getUsername());
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driver.setPhone(driverDTO.getPhone());
        driverRepository.save(driver);
        return driverWithoutPasswordMapper.toDTO(driver);
    }

    @Transactional
    public DriverWithIdDTO changeDriver(Long id, UpdateDriverDTO updateDriverDTO) {
        Driver driver = findActiveDriverById(id);

        if (updateDriverDTO.getUsername() != null && !updateDriverDTO.getUsername().isBlank()) {
            checkUsername(driver, updateDriverDTO.getUsername());
            driver.setUsername(updateDriverDTO.getUsername());
        }

        if (updateDriverDTO.getPhone() != null && !updateDriverDTO.getPhone().isBlank()) {
            checkPhone(driver, updateDriverDTO.getPhone());
            driver.setPhone(updateDriverDTO.getPhone());
        }

        if (updateDriverDTO.getName() != null && !updateDriverDTO.getName().isBlank()) {
            driver.setName(updateDriverDTO.getName());
        }

        if (updateDriverDTO.getPassword() != null && !updateDriverDTO.getPassword().isBlank()) {
            checkPassword(driver, updateDriverDTO.getPassword());
            driver.setPassword(passwordEncoder.encode(updateDriverDTO.getPassword()));
        }

        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

    private void checkUsername(Driver driver, String username) {
        if (driverRepository.findByUsername(username).isPresent() &&
                !username.equals(driver.getUsername())) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
    }

    private void checkPhone(Driver driver, String phone) {
        if (driverRepository.findByPhone(phone).isPresent() &&
                !phone.equals(driver.getPhone())) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
    }

    private void checkPassword(Driver driver, String password) {
        if (passwordEncoder.matches(password, driver.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }
    }

    public Page<RideWithIdDTO> getAvailableRides (Integer page, Integer size)
    {
        return rideServiceClient.getAvailableRides(page,size);
    }

    public Page<RideWithIdDTO> getCompletedRides (Long driverId,Integer page, Integer size)
    {
        return rideServiceClient.getCompletedRides(driverId,page,size);
    }

    public Page<DriverFeedbackWithIdDTO> getAllFeedbacks (Long driverId, Integer page, Integer size)
    {
        return driverFeedbackServiceClient.getFeedbacks(driverId,page,size);
    }

    public RateDTO getDriverRate(Long driverId)
    {
        return driverFeedbackServiceClient.getDriverRate(driverId);
    }

    public DriverFeedbackWithIdDTO changeFeedback(Long feedbackId, UpdateDriverRateDTO updateDriverRateDTO) {
        return driverFeedbackServiceClient.changeFeedBack(feedbackId,updateDriverRateDTO);
    }

    public DriverFeedbackWithIdDTO createFeedback(DriverFeedbackDTO driverFeedbackDTO)
    {
        return driverFeedbackServiceClient.createDriverFeedback(driverFeedbackDTO);
    }

    private CarWithIdDTO getCar (Long carId)
    {
        return carServiceClient.getCarById(carId);
    }

    @Transactional
    public DriverWithIdDTO assignCarToDriver(Long driverId, Long carId)
    {
        Optional<Driver> driverOptional = driverRepository.findDriverByCarId(carId);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        if (driverOptional.isPresent()) {
            throw new EntityExistsException("This car is already assigned to another driver.");
        }
        if (rideServiceClient.getActiveRide(driverId).getId() != null) {
            throw new IllegalStateException("Driver already has an active ride. Cannot assign a car until the current ride is completed.");
        }
        CarWithIdDTO car = getCar(carId);
        RateDTO rateDTO = getDriverRate(driverId);
        if (car.getCategory().equals(Category.BUSINESS.name()) && rateDTO.getRate() >= 4.5) {
            driver.setCarId(car.getId());
        } else if (car.getCategory().equals(Category.COMFORT.name()) && rateDTO.getRate() >= 3.0) {
            driver.setCarId(car.getId());
        } else if (car.getCategory().equals(Category.ECONOMY.name())) {
            driver.setCarId(car.getId());
        } else {
            throw new IllegalStateException("Driver cannot be assigned this car based on rating or category.");
        }
        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

    @Transactional
    public DriverWithIdDTO unassignCarFromDriver(Long driverId, Long carId)
    {
        Optional<Driver> driverOptional = driverRepository.findDriverByCarId(carId);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        if (driverOptional.isEmpty()) {
            throw new EntityNotFoundException("Car is not assigned to any driver.");
        }
        if (rideServiceClient.getActiveRide(driverId).getId() != null) {
            throw new IllegalStateException("Driver already has an active ride. Cannot unassign car until the current ride is completed.");
        }
        if (!driver.getId().equals(driverId))
        {
            throw new IllegalStateException("This car is currently assigned to another driver.");
        }
        driver.setCarId(null);
        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }
}
