package driverservice.service;

import driverservice.client.CarServiceClient;
import driverservice.client.DriverFeedbackServiceClient;
import driverservice.client.RideServiceClient;
import driverservice.dto.*;
import driverservice.entity.Driver;
import driverservice.enums.Category;
import driverservice.exception.SamePasswordException;
import driverservice.kafkaservice.CancelRideByDriverProducer;
import driverservice.kafkaservice.FinishRideProducer;
import driverservice.kafkaservice.RideInProgressProducer;
import driverservice.mapper.DriverMapper;
import driverservice.mapper.DriverWithIdMapper;
import driverservice.mapper.DriverWithoutPasswordMapper;
import driverservice.repository.DriverRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DriverService {
    private static final Logger log = LoggerFactory.getLogger(DriverService.class);

    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverMapper driverMapper = DriverMapper.INSTANCE;
    private final DriverWithoutPasswordMapper driverWithoutPasswordMapper = DriverWithoutPasswordMapper.INSTANCE;
    private final DriverWithIdMapper driverWithIdMapper = DriverWithIdMapper.INSTANCE;

    private final RideServiceClient rideServiceClient;
    private final DriverFeedbackServiceClient driverFeedbackServiceClient;
    private final CarServiceClient carServiceClient;

    private final CancelRideByDriverProducer cancelRideByDriverProducer;
    private final RideInProgressProducer rideInProgressProducer;
    private final FinishRideProducer finishRideProducer;

    private final KeycloakDriverService keycloakDriverService;

    public DriverService(DriverRepository driverRepository, PasswordEncoder passwordEncoder,
                         RideServiceClient rideServiceClient, DriverFeedbackServiceClient driverFeedbackServiceClient,
                         CarServiceClient carServiceClient,CancelRideByDriverProducer cancelRideByDriverProducer,
                         RideInProgressProducer rideInProgressProducer,FinishRideProducer finishRideProducer,
                         KeycloakDriverService keycloakDriverService) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
        this.rideServiceClient = rideServiceClient;
        this.driverFeedbackServiceClient=driverFeedbackServiceClient;
        this.carServiceClient=carServiceClient;
        this.cancelRideByDriverProducer=cancelRideByDriverProducer;
        this.rideInProgressProducer=rideInProgressProducer;
        this.finishRideProducer=finishRideProducer;
        this.keycloakDriverService=keycloakDriverService;
    }

    public DriverWithIdDTO createDriver(DriverDTO driverDTO) {
        log.info("Creating driver with username: {}", driverDTO.getUsername());

        if (driverRepository.findByUsername(driverDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
        if (driverRepository.findByPhone(driverDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
        String keycloakId = keycloakDriverService.registerDriver(driverDTO);
        log.info("User registered in Keycloak with ID: {}", keycloakId);

        Driver driver = driverMapper.toEntity(driverDTO);
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

    public TokenResponseDTO loginDriver (AuthDTO authDTO)
    {
        return keycloakDriverService.loginDriver(authDTO);
    }

    @Transactional
    public void deleteById(Long id) {
        log.info("Deleting driver with id: {}", id);
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
        log.info("Retrieving profile for driver ID: {}", id);
        Driver driver = findActiveDriverById(id);
        return driverWithIdMapper.toDTO(driver);
    }

    public Page<DriverWithIdDTO> getAllDrivers(Pageable pageable) {
        log.info("Retrieving all driver profiles, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return driverRepository.findAll(pageable).map(driverWithIdMapper::toDTO);
    }

    @Transactional
    public DriverWithoutPasswordDTO updateProfile(Long id, DriverDTO driverDTO) {
        log.info("Updating profile for driver ID: {}", id);
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
        log.info("Partially updating driver with ID: {}", id);
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
        log.info("Fetching available rides, page={}, size={}", page, size);
        return rideServiceClient.getAvailableRides(page,size);
    }

    public Page<RideWithIdDTO> getCompletedRides (Long driverId,Integer page, Integer size)
    {
        log.info("Fetching completed rides for driver ID: {}, page={}, size={}", driverId, page, size);
        driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        return rideServiceClient.getCompletedRides(driverId,page,size);
    }

    public Page<DriverFeedbackWithIdDTO> getAllFeedbacks (Long driverId, Integer page, Integer size)
    {
        log.info("Retrieving all feedbacks for driver ID: {}, page={}, size={}", driverId, page, size);

        driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        return driverFeedbackServiceClient.getFeedbacks(driverId,page,size);
    }

    public RateDTO getDriverRate(Long driverId)
    {
        log.info("Getting driver rating for driver ID: {}", driverId);

        driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        return driverFeedbackServiceClient.getDriverRate(driverId);
    }

    public DriverFeedbackWithIdDTO changeFeedback(Long driverId,Long feedbackId, UpdateDriverRateDTO updateDriverRateDTO) {
        log.info("Updating feedback with ID: {}", feedbackId);
        findActiveDriverById(driverId);
        return driverFeedbackServiceClient.changeFeedBack(feedbackId,updateDriverRateDTO);
    }

    public DriverFeedbackWithIdDTO createFeedback(Long driverId,DriverFeedbackDTO driverFeedbackDTO)
    {
        log.info("Creating feedback for driver ID: {}", driverId);
        findActiveDriverById(driverId);
        driverFeedbackDTO.setDriverId(driverId);
        return driverFeedbackServiceClient.createDriverFeedback(driverFeedbackDTO);
    }

    private CarWithIdDTO getCar (Long carId)
    {
        return carServiceClient.getCarById(carId);
    }

    @Transactional
    public DriverWithIdDTO assignCarToDriver(Long driverId, Long carId)
    {
        log.info("Assign car with ID {} to driver with ID {}", carId, driverId);
        Optional<Driver> driverOptional = driverRepository.findDriverByCarId(carId);
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        checkIsDeleted(driver.getIsDeleted());
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
        log.info("Unassign car with ID {} from driver with ID {}", carId, driverId);
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

    public RideWithIdDTO applyRide(Long rideId,Long driverId)
    {
        log.info("Driver with ID {} is applying for ride with ID {}", driverId, rideId);

        Driver driver = findActiveDriverById(driverId);
        if (driver.getCarId() == null)
        {
            throw new IllegalStateException("Driver has no assigned car.");
        }

        CarAndDriverIdDTO carAndDriverIdDTO= new CarAndDriverIdDTO(driver.getCarId(),driverId);
        return rideServiceClient.applyRide(rideId,carAndDriverIdDTO);
    }

    public void cancelRide(CanceledRideByDriverDTO canceledRideByDriverDTO) {
        log.info("Driver with ID {} is cancelling ride", canceledRideByDriverDTO.getDriverId());

        findActiveDriverById(canceledRideByDriverDTO.getDriverId());
        cancelRideByDriverProducer.sendCancelRequest(canceledRideByDriverDTO);
    }

    public void startRide(RideInProgressDTO rideInProgressDTO) {
        log.info("Driver with ID {} is starting ride with ID {}", rideInProgressDTO.getDriverId(), rideInProgressDTO.getRideId());

        findActiveDriverById(rideInProgressDTO.getDriverId());
        rideInProgressProducer.sendRideInProgressRequest(rideInProgressDTO);
    }

    public void finishRide(FinishRideDTO finishRideDTO)
    {
        log.info("Driver with ID {} is finishing ride with ID {}", finishRideDTO.getDriverId(), finishRideDTO.getRideId());

        findActiveDriverById(finishRideDTO.getDriverId());
        finishRideProducer.sendFinishRequest(finishRideDTO);
    }

    public Page<RideWithIdDTO> getCompletedRidesPeriod(Long driverId, LocalDateTime start, LocalDateTime end, int page, int size)
    {
        log.info("Fetching completed rides for driver ID {} from {} to {}", driverId, start, end);
        driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        return rideServiceClient.getCompletedRidesPeriod(driverId,start,end,page,size);
    }

    public EarningDTO getEarnings(Long driverId, LocalDateTime start, LocalDateTime end)
    {
        log.info("Fetching earnings for driver ID {} from {} to {}", driverId, start, end);
        driverRepository.findById(driverId).orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        return rideServiceClient.getEarnings(driverId,start,end);
    }
}
