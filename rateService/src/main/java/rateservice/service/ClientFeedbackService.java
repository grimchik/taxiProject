package rateservice.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rateservice.client.RideServiceClient;
import rateservice.dto.*;
import rateservice.entity.ClientFeedback;
import rateservice.entity.DriverFeedback;
import rateservice.mapper.ClientFeedbackWithIdMapper;
import rateservice.repository.ClientFeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rateservice.repository.DriverFeedbackRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientFeedbackService {

    private static final Logger log = LoggerFactory.getLogger(ClientFeedbackService.class);

    private final ClientFeedbackRepository clientFeedbackRepository;
    private final DriverFeedbackRepository driverFeedbackRepository;
    private final ClientFeedbackWithIdMapper clientFeedbackWithIdMapper = ClientFeedbackWithIdMapper.INSTANCE;
    private final RideServiceClient rideServiceClient;

    public ClientFeedbackService(ClientFeedbackRepository clientFeedbackRepository,
                                 DriverFeedbackRepository driverFeedbackRepository,
                                 RideServiceClient rideServiceClient) {
        this.clientFeedbackRepository = clientFeedbackRepository;
        this.rideServiceClient = rideServiceClient;
        this.driverFeedbackRepository = driverFeedbackRepository;
    }

    @Transactional
    public ClientFeedbackWithIdDTO createFeedback(ClientFeedbackDTO clientFeedbackDTO) {
        log.info("Creating feedback for rideId={}, userId={}", clientFeedbackDTO.getRideId(), clientFeedbackDTO.getUserId());

        Optional<ClientFeedback> existingFeedback = clientFeedbackRepository.findByRideIdAndUserId(
                clientFeedbackDTO.getRideId(), clientFeedbackDTO.getUserId());

        if (existingFeedback.isPresent()) {
            throw new EntityExistsException("Feedback already exists for this ride.");
        }

        RideWithIdDTO ride = getRide(clientFeedbackDTO.getRideId());
        if (ride == null || !ride.getStatus().equalsIgnoreCase("COMPLETED")) {
            throw new IllegalStateException("Can't create feedback. Ride is not completed.");
        }

        if (!ride.getUserId().equals(clientFeedbackDTO.getUserId())) {
            throw new IllegalStateException("User is not authorized to provide feedback for this ride.");
        }

        ClientFeedback clientFeedback = new ClientFeedback();
        clientFeedback.setRate(clientFeedbackDTO.getRate());
        clientFeedback.setComment(clientFeedbackDTO.getComment());
        clientFeedback.setCleanInterior(clientFeedbackDTO.getCleanInterior());
        clientFeedback.setSafeDriving(clientFeedbackDTO.getSafeDriving());
        clientFeedback.setNiceMusic(clientFeedbackDTO.getNiceMusic());
        clientFeedback.setRideId(clientFeedbackDTO.getRideId());
        clientFeedback.setUserId(clientFeedbackDTO.getUserId());

        clientFeedbackRepository.save(clientFeedback);
        log.info("Feedback created with id={} for userId={}", clientFeedback.getId(), clientFeedback.getUserId());

        return clientFeedbackWithIdMapper.toDTO(clientFeedback);
    }

    public ClientFeedbackWithIdDTO getFeedback(Long id) {
        log.info("Retrieving client feedback with id={}", id);
        Optional<ClientFeedback> feedbackOptional = clientFeedbackRepository.findById(id);
        feedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));
        return clientFeedbackWithIdMapper.toDTO(feedbackOptional.get());
    }

    @Transactional
    public ClientFeedbackWithIdDTO changeClientFeedback(Long id, UpdateClientRateDTO updateClientRateDTO) {
        log.info("Partially updating client feedback with id={}", id);
        Optional<ClientFeedback> feedbackOptional = clientFeedbackRepository.findById(id);
        feedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));

        ClientFeedback feedback = feedbackOptional.get();

        if (updateClientRateDTO.getCleanInterior() != null) feedback.setCleanInterior(updateClientRateDTO.getCleanInterior());
        if (updateClientRateDTO.getNiceMusic() != null) feedback.setNiceMusic(updateClientRateDTO.getNiceMusic());
        if (updateClientRateDTO.getSafeDriving() != null) feedback.setSafeDriving(updateClientRateDTO.getSafeDriving());
        if (updateClientRateDTO.getRate() != null) feedback.setRate(updateClientRateDTO.getRate());
        if (updateClientRateDTO.getComment() != null) feedback.setComment(updateClientRateDTO.getComment());

        clientFeedbackRepository.save(feedback);
        log.info("Feedback with id={} successfully updated", id);

        return clientFeedbackWithIdMapper.toDTO(feedback);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        log.info("Deleting client feedback with id={}", id);
        Optional<ClientFeedback> feedbackOptional = clientFeedbackRepository.findById(id);
        feedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));
        clientFeedbackRepository.delete(feedbackOptional.get());
        log.info("Feedback with id={} deleted successfully", id);
    }

    @Transactional
    public ClientFeedbackWithIdDTO updateFeedback(Long id, ClientFeedbackDTO dto) {
        log.info("Updating full client feedback with id={}", id);
        Optional<ClientFeedback> feedbackOptional = clientFeedbackRepository.findById(id);
        feedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));

        ClientFeedback feedback = feedbackOptional.get();

        feedback.setRate(dto.getRate());
        feedback.setComment(dto.getComment());
        feedback.setCleanInterior(dto.getCleanInterior());
        feedback.setSafeDriving(dto.getSafeDriving());
        feedback.setNiceMusic(dto.getNiceMusic());

        clientFeedbackRepository.save(feedback);
        log.info("Feedback with id={} updated completely", id);

        return clientFeedbackWithIdMapper.toDTO(feedback);
    }

    public Page<ClientFeedbackWithIdDTO> getAllClientFeedbacks(Pageable pageable) {
        log.info("Retrieving all client feedbacks with pagination");
        return clientFeedbackRepository.findAll(pageable).map(clientFeedbackWithIdMapper::toDTO);
    }

    public Page<ClientFeedbackWithIdDTO> getAllClientFeedbacksById(Long userId, Pageable pageable) {
        log.info("Retrieving all feedbacks for userId={}", userId);
        return clientFeedbackRepository.findAllByUserId(userId, pageable).map(clientFeedbackWithIdMapper::toDTO);
    }

    public RateDTO calculateAverageRating(Long userId) {
        log.info("Calculating average rating for userId={}", userId);
        Page<RideWithIdDTO> rides = rideServiceClient.getRides(userId, 0, 50);

        List<Long> rideIds = rides.getContent()
                .stream()
                .map(RideWithIdDTO::getId)
                .collect(Collectors.toList());

        List<DriverFeedback> feedbacks = driverFeedbackRepository.findByRideIdIn(rideIds);

        if (feedbacks.isEmpty()) {
            log.info("No feedbacks found for userId={}, returning 0.0", userId);
            return new RateDTO(0.0);
        }

        double totalRate = 0;
        double totalAttributes = 0;
        int count = 0;

        for (DriverFeedback feedback : feedbacks) {
            totalRate += feedback.getRate();
            totalAttributes += getValidAttributeValue(feedback.getPunctuality());
            totalAttributes += getValidAttributeValue(feedback.getCleanPassenger());
            totalAttributes += getValidAttributeValue(feedback.getPolitePassenger());
            count++;
        }

        double averageRate = totalRate / count - 1.0;
        double averageAttributes = totalAttributes / (count * 3);

        double finalRate = averageRate + averageAttributes;
        log.info("Calculated average rate for userId={} is {}", userId, finalRate);

        return new RateDTO(finalRate);
    }

    private double getValidAttributeValue(Boolean attribute) {
        return (attribute != null && attribute) ? 1.0 : 0.0;
    }

    private RideWithIdDTO getRide(Long rideId) {
        log.info("Fetching ride data for rideId={}", rideId);
        return rideServiceClient.getRide(rideId);
    }
}
