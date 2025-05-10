package rideservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rideservice.dto.*;
import rideservice.entity.Location;
import rideservice.entity.Ride;
import rideservice.enums.Status;
import rideservice.exception.ActiveRideException;
import rideservice.kafkaservice.producer.CreatePaymentProducer;
import rideservice.mapper.LocationMapper;
import rideservice.mapper.RideWithIdMapper;
import rideservice.repository.RideRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RideService {
    private static final Logger log = LoggerFactory.getLogger(RideService.class);

    private final RideRepository rideRepository;
    private final RideWithIdMapper rideWithIdMapper = RideWithIdMapper.INSTANCE;
    private final LocationMapper locationMapper = LocationMapper.INSTANCE;

    private final CreatePaymentProducer createPaymentProducer;

    public RideService(RideRepository rideRepository,CreatePaymentProducer createPaymentProducer)
    {
        this.rideRepository=rideRepository;
        this.createPaymentProducer=createPaymentProducer;
    }
    private Double calculatePrice(List<?> locations) {
        return (double) locations.size() * 5.5;
    }


    @Transactional
    public RideWithIdDTO createRide(RideDTO rideDTO)
    {
        List<String> activeStatuses = Arrays.asList(
                Status.REQUESTED.name(),
                Status.WAITING_DRIVER.name(),
                Status.DRIVER_ON_THE_WAY.name(),
                Status.IN_PROGRESS.name(),
                Status.WAITING_PAYMENT.name()
        );
        log.info("Attempting to create a ride for userId={}", rideDTO.getUserId());

        Optional<Ride> existingRide = rideRepository.findByUserIdAndStatusIn(rideDTO.getUserId(), activeStatuses);

        if (existingRide.isPresent()) {
            log.warn("User {} already has an active ride with status {}", rideDTO.getUserId(), existingRide.get().getStatus());
            throw new ActiveRideException("User already has an active ride with status: " + existingRide.get().getStatus());
        }

        Ride ride = new Ride();
        ride.setUserId(rideDTO.getUserId());
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
        log.info("Ride created for userId={}, rideId={}", ride.getUserId(), ride.getId());

        return rideWithIdMapper.toDTO(ride);
    }

    @Transactional
    public RideWithIdDTO applyRide(Long rideId,CarAndDriverIdDTO carAndDriverIdDTO)
    {
        log.info("Attempting to apply ride with rideId={} for driverId={}", rideId, carAndDriverIdDTO.getDriverId());

        Optional<Ride> rideOptional = rideRepository.findById(rideId);
        Ride ride = rideOptional.orElseThrow(() -> new EntityNotFoundException("Ride not found"));

        if (ride.getDriverId() != null)
        {
            log.warn("This ride with rideId={} already has a driver assigned", rideId);

            throw new IllegalStateException("This ride already has a driver assigned.");
        }

        Set<String> allowedStatuses = Set.of(Status.REQUESTED.name(), Status.WAITING_DRIVER.name());
        if (!allowedStatuses.contains(ride.getStatus())) {
            log.warn("Ride with rideId={} cannot be applied, current status: {}", rideId, ride.getStatus());

            throw new IllegalStateException("Ride is not in a state that allows applying.");
        }

        if (getActiveRide(carAndDriverIdDTO.getDriverId()).getId() != null)
        {
            log.warn("Driver {} already has an active ride", carAndDriverIdDTO.getDriverId());

            throw new IllegalStateException("Driver already has an active ride.");
        }

        ride.setStatus(Status.DRIVER_ON_THE_WAY.name());
        ride.setDriverId(carAndDriverIdDTO.getDriverId());
        ride.setCarId(carAndDriverIdDTO.getCarId());
        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully applied for driverId={}", rideId, carAndDriverIdDTO.getDriverId());

        return rideWithIdMapper.toDTO(ride);
    }

    @Transactional
    public RideWithIdDTO changeRide(Long id, UpdateRideDTO updateRideDTO) {
        log.info("Attempting to change ride with rideId={}", id);

        Optional<Ride> rideOptional = rideRepository.findById(id);
        rideOptional.orElseThrow(() -> new EntityNotFoundException("Ride not found"));
        Ride ride = rideOptional.get();
        if (!ride.getUserId().equals(updateRideDTO.getUserId()))
        {
            log.warn("User {} is not authorized to modify ride with rideId={}", updateRideDTO.getUserId(), id);

            throw new AccessDeniedException("This User don't have permission to modify this ride");
        }

        if (!isStatusEditable(ride)) {
            log.warn("Ride with rideId={} cannot be modified, current status: {}", id, ride.getStatus());

            throw new IllegalStateException("Cannot modify a ride in the current status: " + ride.getStatus());
        }

        if (updateRideDTO.getLocations() != null)
        {
            for (LocationDTO locationDTO : updateRideDTO.getLocations()) {
                Location location = locationMapper.toEntity(locationDTO);
                location.setRide(ride);
                ride.getLocations().add(location);
            }
            ride.setPrice(calculatePrice(updateRideDTO.getLocations()));
        }
        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully updated", id);

        return rideWithIdMapper.toDTO(ride);
    }

    private boolean isStatusEditable(Ride ride) {
        return !(ride.getStatus().equals(Status.IN_PROGRESS.name()) ||
                ride.getStatus().equals(Status.WAITING_PAYMENT.name()) ||
                ride.getStatus().equals(Status.COMPLETED.name()) ||
                ride.getStatus().equals(Status.CANCELED_BY_USER.name()));
    }

    @Transactional
    public void deleteRideById(Long id)
    {
        log.info("Attempting to delete ride with rideId={}", id);

        Optional<Ride> rideOptional = rideRepository.findById(id);
        rideOptional.orElseThrow(() -> new EntityNotFoundException("Ride not found"));
        Ride ride = rideOptional.get();
        rideRepository.delete(ride);
        log.info("Ride with rideId={} successfully deleted", id);
    }

    public RideWithIdDTO getRideById(Long id) {
        log.info("Fetching ride with rideId={}", id);

        Optional<Ride> rideOptional = rideRepository.findById(id);
        rideOptional.orElseThrow(() -> new EntityNotFoundException("Ride not found"));
        Ride ride = rideOptional.get();
        return rideWithIdMapper.toDTO(ride);
    }
    public Page<RideWithIdDTO> getAllRides(Pageable pageable) {
        log.info("Fetching all rides with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return rideRepository.findAll(pageable).map(rideWithIdMapper::toDTO);
    }

    public Page<RideWithIdDTO> getAllRidesByUserId(Long userId, Pageable pageable) {
        log.info("Fetching all rides for userId={} with pagination: page {}, size {}", userId, pageable.getPageNumber(), pageable.getPageSize());
        return rideRepository.findAllByUserId(userId, pageable).map(rideWithIdMapper::toDTO);
    }

    public Page<RideWithIdDTO> getAllRidesByDriverId(Long driverId, Pageable pageable) {
        log.info("Fetching all rides for driverId={} with pagination: page {}, size {}", driverId, pageable.getPageNumber(), pageable.getPageSize());
        return rideRepository.findAllByDriverId(driverId, pageable).map(rideWithIdMapper::toDTO);
    }

    public Page<RideWithIdDTO> getAllAvailableRides(Pageable pageable) {
        log.info("Fetching all available rides with pagination: page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        return rideRepository.findByStatus(Status.REQUESTED.name(), pageable).map(rideWithIdMapper::toDTO);
    }

    public Page<RideWithIdDTO> getCompletedRides(Long driverId, Pageable pageable) {
        log.info("Fetching completed rides for driverId={} with pagination: page {}, size {}", driverId, pageable.getPageNumber(), pageable.getPageSize());
        return rideRepository.findByStatusAndDriverId(Status.COMPLETED.name(), driverId, pageable).map(rideWithIdMapper::toDTO);
    }

    public Page<RideWithIdDTO> getCompletedRidesPeriod(Long driverId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        log.info("Fetching completed rides for driverId={} between {} and {} with pagination: page {}, size {}", driverId, start, end, pageable.getPageNumber(), pageable.getPageSize());
        return rideRepository.findByDriverIdAndStatusAndCreatedAtBetween(driverId, Status.COMPLETED.name(), start, end, pageable).map(rideWithIdMapper::toDTO);
    }

    public EarningDTO getTotalEarnings(Long driverId, LocalDateTime start, LocalDateTime end) {
        log.info("Fetching total earnings for driverId={} between {} and {}", driverId, start, end);
        return new EarningDTO(rideRepository.getTotalEarnings(driverId, Status.COMPLETED.name(), start, end));
    }

    public RideWithIdDTO getActiveRide(Long driverId) {
        log.info("Fetching active ride for driverId={}", driverId);

        List<String> activeStatuses = Arrays.asList(
                Status.REQUESTED.name(),
                Status.WAITING_DRIVER.name(),
                Status.DRIVER_ON_THE_WAY.name(),
                Status.IN_PROGRESS.name()
        );

        Optional<Ride> existingRide = rideRepository.findByDriverIdAndStatusIn(driverId, activeStatuses);

        if (existingRide.isPresent()) {
            return rideWithIdMapper.toDTO(existingRide.get());
        } else {
            return new RideWithIdDTO();
        }
    }

    @Transactional
    public void cancelRide(CanceledRideDTO canceledRideDTO) {
        log.info("Attempting to cancel ride with rideId={} for userId={}", canceledRideDTO.getRideId(), canceledRideDTO.getUserId());

        Ride ride = rideRepository.findById(canceledRideDTO.getRideId())
                .orElseThrow(() -> new EntityNotFoundException("Ride not found with id: " + canceledRideDTO.getRideId()));

        if (!ride.getUserId().equals(canceledRideDTO.getUserId())) {
            log.warn("User {} is not authorized to cancel this ride", canceledRideDTO.getUserId());
            throw new IllegalStateException("User is not authorized to cancel this ride");
        }

        if (!isCancelable(ride.getStatus())) {
            log.warn("This ride with rideId={} cannot be canceled", canceledRideDTO.getRideId());
            throw new IllegalStateException("This ride cannot be canceled because it has already started or is in progress");
        }

        ride.setStatus(Status.CANCELED_BY_USER.name());
        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully canceled by userId={}", canceledRideDTO.getRideId(), canceledRideDTO.getUserId());
    }

    private boolean isCancelable(String status) {
        return status.equals(Status.REQUESTED.name()) ||
                status.equals(Status.WAITING_DRIVER.name()) ||
                status.equals(Status.DRIVER_ON_THE_WAY.name());
    }

    @Transactional
    public void applyPromoCode(ApplyPromocodeDTO applyPromocodeDTO) {
        log.info("Attempting to apply promo code for userId={}", applyPromocodeDTO.getUserId());

        Optional<Ride> activeRide = rideRepository.findByUserIdAndStatusIn(
                applyPromocodeDTO.getUserId(),
                Arrays.asList(Status.REQUESTED.name(), Status.WAITING_DRIVER.name(), Status.DRIVER_ON_THE_WAY.name(), Status.IN_PROGRESS.name())
        );

        Ride ride = activeRide.orElseThrow(() ->
                new EntityNotFoundException("No active ride found for user with id: " + applyPromocodeDTO.getUserId())
        );

        if (ride.getPromoCodeApplied()) {
            log.warn("Promo code has already been applied to this ride.");
            throw new IllegalStateException("Promo code has already been applied to this ride.");
        }

        ride.setPrice(ride.getPrice() * (1.0 - applyPromocodeDTO.getPercent() / 100.0));
        ride.setPromoCodeApplied(true);
        rideRepository.save(ride);
        log.info("Promo code applied successfully to ride with userId={}", applyPromocodeDTO.getUserId());
    }

    @Transactional
    public void cancelRideByDriver(CanceledRideByDriverDTO canceledRideByDriverDTO) {
        log.info("Attempting to cancel ride with rideId={} for driverId={}", canceledRideByDriverDTO.getRideId(), canceledRideByDriverDTO.getDriverId());

        Ride ride = rideRepository.findById(canceledRideByDriverDTO.getRideId())
                .orElseThrow(() -> new EntityNotFoundException("Ride not found with id: " + canceledRideByDriverDTO.getRideId()));

        if (!ride.getDriverId().equals(canceledRideByDriverDTO.getDriverId())) {
            log.warn("Driver {} is not authorized to cancel this ride", canceledRideByDriverDTO.getDriverId());
            throw new IllegalStateException("Driver is not authorized to cancel this ride");
        }

        if (!isCancelable(ride.getStatus())) {
            log.warn("This ride with rideId={} cannot be canceled", canceledRideByDriverDTO.getRideId());
            throw new IllegalStateException("This ride cannot be canceled because it has already started or is in progress");
        }

        ride.setCarId(null);
        ride.setDriverId(null);
        ride.setStatus(Status.WAITING_DRIVER.name());
        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully canceled by driverId={}", canceledRideByDriverDTO.getRideId(), canceledRideByDriverDTO.getDriverId());
    }

    @Transactional
    public void rideInProgress(RideInProgressDTO message) {
        log.info("Marking ride with rideId={} as in progress for driverId={}", message.getRideId(), message.getDriverId());

        Ride ride = rideRepository.findById(message.getRideId())
                .orElseThrow(() -> new EntityNotFoundException("Ride not found with id: " + message.getRideId()));

        if (!ride.getDriverId().equals(message.getDriverId())) {
            log.warn("Driver {} is not authorized to mark this ride as in progress", message.getDriverId());
            throw new IllegalStateException("Driver is not authorized to cancel this ride");
        }

        if (!ride.getStatus().equals(Status.DRIVER_ON_THE_WAY.name())) {
            log.warn("Ride with rideId={} cannot be marked as in progress, current status: {}", message.getRideId(), ride.getStatus());
            throw new IllegalStateException("Ride status must be DRIVER_ON_THE_WAY to be marked as IN_PROGRESS");
        }

        ride.setStatus(Status.IN_PROGRESS.name());
        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully marked as IN_PROGRESS", message.getRideId());
    }

    @Transactional
    public void finishRide(FinishRideDTO message) {
        log.info("Finishing ride with rideId={} for driverId={}", message.getRideId(), message.getDriverId());

        Ride ride = rideRepository.findById(message.getRideId())
                .orElseThrow(() -> new EntityNotFoundException("Ride not found with id: " + message.getRideId()));

        if (!ride.getDriverId().equals(message.getDriverId())) {
            log.warn("Driver {} is not authorized to finish ride with rideId={}", message.getDriverId(), message.getRideId());

            throw new IllegalStateException("Driver is not authorized to cancel this ride");
        }

        if (!ride.getStatus().equals(Status.IN_PROGRESS.name()))
        {
            log.warn("Ride with rideId={} cannot be finished, current status: {}", message.getRideId(), ride.getStatus());

            throw new IllegalStateException("Ride status must be IN_PROGRESS to be marked as IN_PROGRESS");
        }

        ride.setStatus(Status.WAITING_PAYMENT.name());

        createPaymentProducer.sendCreatePaymentRequest(new CreatePaymentDTO(ride.getPrice(),ride.getId(),ride.getUserId()));

        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully marked as WAITING_PAYMENT", message.getRideId());

    }

    @Transactional
    public void completeRide(CompleteRideDTO message) {
        log.info("Completing ride with rideId={}", message.getRideId());

        Ride ride = rideRepository.findById(message.getRideId())
                .orElseThrow(() -> new EntityNotFoundException("Ride not found with id: " + message.getRideId()));

        if (!ride.getStatus().equals(Status.WAITING_PAYMENT.name())) {
            log.warn("Ride with rideId={} cannot be marked as completed, current status: {}", message.getRideId(), ride.getStatus());
            throw new IllegalStateException("Ride status must be WAITING_PAYMENT to be marked as WAITING_PAYMENT");
        }

        ride.setStatus(Status.COMPLETED.name());
        rideRepository.save(ride);
        log.info("Ride with rideId={} successfully marked as COMPLETED", message.getRideId());
    }
}
