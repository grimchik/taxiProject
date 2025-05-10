package rateservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rateservice.client.RideServiceClient;
import rateservice.dto.*;
import rateservice.entity.ClientFeedback;
import rateservice.entity.DriverFeedback;
import rateservice.mapper.DriverFeedbackMapper;
import rateservice.mapper.DriverFeedbackWithIdMapper;
import rateservice.repository.ClientFeedbackRepository;
import rateservice.repository.DriverFeedbackRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DriverFeedbackService {
    private static final Logger log = LoggerFactory.getLogger(DriverFeedbackService.class);

    private final ClientFeedbackRepository clientFeedbackRepository;
    private final DriverFeedbackRepository driverFeedbackRepository;
    private final DriverFeedbackWithIdMapper driverFeedbackWithIdMapper = DriverFeedbackWithIdMapper.INSTANCE;

    private final RideServiceClient rideServiceClient;
    public DriverFeedbackService(DriverFeedbackRepository driverFeedbackRepository,RideServiceClient rideServiceClient,
                                 ClientFeedbackRepository clientFeedbackRepository)
    {
        this.driverFeedbackRepository=driverFeedbackRepository;
        this.rideServiceClient=rideServiceClient;
        this.clientFeedbackRepository=clientFeedbackRepository;
    }


    @Transactional
    public DriverFeedbackWithIdDTO createFeedback(DriverFeedbackDTO driverFeedbackDTO)
    {
        log.info("Creating driver feedback for rideId={}, driverId={}", driverFeedbackDTO.getRideId(), driverFeedbackDTO.getDriverId());

        Optional<DriverFeedback> existingFeedback = driverFeedbackRepository.findByRideIdAndDriverId(driverFeedbackDTO.getRideId(), driverFeedbackDTO.getDriverId());

        if (existingFeedback.isPresent())
        {
            throw new EntityExistsException("Feedback already exists for this ride.");
        }

        DriverFeedback driverFeedback=new DriverFeedback();
        RideWithIdDTO ride = getRide(driverFeedbackDTO.getRideId());

        if (ride == null || !ride.getStatus().equalsIgnoreCase("COMPLETED"))
        {
            throw new IllegalStateException("Can't create feedback. Ride is not completed.");
        }

        if (!ride.getDriverId().equals(driverFeedbackDTO.getDriverId())) {
            throw new IllegalStateException("Driver is not authorized to provide feedback for this ride.");
        }

        driverFeedback.setRate(driverFeedbackDTO.getRate());
        driverFeedback.setComment(driverFeedbackDTO.getComment());
        driverFeedback.setPunctuality(driverFeedbackDTO.getPunctuality());
        driverFeedback.setCleanPassenger(driverFeedbackDTO.getCleanPassenger());
        driverFeedback.setPolitePassenger(driverFeedbackDTO.getPolitePassenger());
        driverFeedback.setRideId(driverFeedbackDTO.getRideId());
        driverFeedback.setDriverId(driverFeedbackDTO.getDriverId());

        driverFeedbackRepository.save(driverFeedback);
        log.info("Driver feedback created for rideId={}, driverId={}", driverFeedback.getRideId(), driverFeedback.getDriverId());

        return driverFeedbackWithIdMapper.toDTO(driverFeedback);
    }

    public DriverFeedbackWithIdDTO getFeedback(Long id)
    {
        log.info("Retrieving driver feedback with id={}", id);

        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        driverFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from driver not found"));
        return driverFeedbackWithIdMapper.toDTO(driverFeedbackOptional.get());
    }

    @Transactional
    public DriverFeedbackWithIdDTO changeDriverFeedback(Long id, UpdateDriverRateDTO updateDriverRateDTO) {
        log.info("Partially updating driver feedback with id={}", id);

        Optional<DriverFeedback> driverFeedbackOptional =driverFeedbackRepository.findById(id);
        driverFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from driver not found"));

        DriverFeedback driverFeedback = driverFeedbackOptional.get();

        if (updateDriverRateDTO.getPunctuality() != null) {
            driverFeedback.setPunctuality(updateDriverRateDTO.getPunctuality());
        }

        if (updateDriverRateDTO.getCleanPassenger() != null) {
            driverFeedback.setCleanPassenger(updateDriverRateDTO.getCleanPassenger());
        }

        if (updateDriverRateDTO.getPolitePassenger() != null) {
            driverFeedback.setPolitePassenger(updateDriverRateDTO.getPolitePassenger());
        }

        if (updateDriverRateDTO.getRate() != null) {
            driverFeedback.setRate(updateDriverRateDTO.getRate());
        }

        if (updateDriverRateDTO.getComment() != null) {
            driverFeedback.setComment(updateDriverRateDTO.getComment());
        }

        driverFeedbackRepository.save(driverFeedback);
        log.info("Driver feedback with id={} updated", id);

        return driverFeedbackWithIdMapper.toDTO(driverFeedback);
    }

    @Transactional
    public void deleteFeedback(Long id)
    {
        log.info("Deleting driver feedback with id={}", id);

        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        driverFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from driver not found"));
        driverFeedbackRepository.delete(driverFeedbackOptional.get());
        log.info("Driver feedback with id={} deleted", id);

    }

    @Transactional
    public DriverFeedbackWithIdDTO updateFeedback(Long id, DriverFeedbackDTO driverFeedbackDTO)
    {
        log.info("Updating driver feedback with id={}", id);

        Optional<DriverFeedback> driverFeedbackOptional = driverFeedbackRepository.findById(id);
        driverFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from driver not found"));

        DriverFeedback driverFeedback = driverFeedbackOptional.get();
        driverFeedback.setRate(driverFeedbackDTO.getRate());
        driverFeedback.setComment(driverFeedbackDTO.getComment());
        driverFeedback.setPunctuality(driverFeedbackDTO.getPunctuality());
        driverFeedback.setCleanPassenger(driverFeedbackDTO.getCleanPassenger());
        driverFeedback.setPolitePassenger(driverFeedbackDTO.getPolitePassenger());

        driverFeedbackRepository.save(driverFeedback);
        log.info("Driver feedback with id={} updated", id);

        return driverFeedbackWithIdMapper.toDTO(driverFeedback);
    }

    public Page<DriverFeedbackWithIdDTO> getAllDriverFeedbacks(Pageable pageable) {
        log.info("Retrieving all driver feedbacks with pagination");

        return driverFeedbackRepository.findAll(pageable).map(driverFeedbackWithIdMapper::toDTO);
    }

    public Page<DriverFeedbackWithIdDTO> getAllDriverFeedbacksById (Long driverId,Pageable pageable) {
        log.info("Retrieving all feedbacks for driverId={} with pagination", driverId);

        return driverFeedbackRepository.findAllByDriverId(driverId,pageable).map(driverFeedbackWithIdMapper::toDTO);
    }

    public RateDTO calculateAverageRating(Long driverId) {
        log.info("Calculating average rating for driverId={}", driverId);

        Page<RideWithIdDTO> rides = rideServiceClient.getRides(driverId,0,50);

        List<Long> rideIds = rides.getContent()
                .stream()
                .map(RideWithIdDTO::getId)
                .collect(Collectors.toList());

        List<ClientFeedback> feedbacks = clientFeedbackRepository.findByRideIdIn(rideIds);

        if (feedbacks.isEmpty()) {
            log.warn("No client feedbacks found for driverId={}", driverId);

            return new RateDTO(0.0);
        }

        double totalRate = 0;
        double totalAttributes = 0;
        int count = 0;

        for (ClientFeedback feedback : feedbacks) {
            totalRate += feedback.getRate();
            totalAttributes += getValidAttributeValue(feedback.getCleanInterior());
            totalAttributes += getValidAttributeValue(feedback.getNiceMusic());
            totalAttributes += getValidAttributeValue(feedback.getSafeDriving());
            count++;
        }

        double averageRate = totalRate / count-1.0;
        double averageAttributes = totalAttributes / (count * 3);
        log.info("Average rating for driverId={} is {}", driverId, averageRate + averageAttributes);

        return new RateDTO(averageRate + averageAttributes);
    }

    private double getValidAttributeValue(Boolean attribute) {
        return (attribute != null && attribute) ? 1.0 : 0.0;
    }
    private RideWithIdDTO getRide(Long rideId)
    {
        return rideServiceClient.getRide(rideId);
    }
}
