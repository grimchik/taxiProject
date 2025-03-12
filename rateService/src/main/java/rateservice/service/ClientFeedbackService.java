package rateservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rateservice.client.RideServiceClient;
import rateservice.dto.*;
import rateservice.entity.ClientFeedback;
import rateservice.mapper.ClientFeedbackMapper;
import rateservice.mapper.ClientFeedbackWithIdMapper;
import rateservice.repository.ClientFeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Service
public class ClientFeedbackService {
    private final ClientFeedbackRepository clientFeedbackRepository;
    private final ClientFeedbackWithIdMapper clientFeedbackWithIdMapper = ClientFeedbackWithIdMapper.INSTANCE;

    private final RideServiceClient rideServiceClient;
    public ClientFeedbackService(ClientFeedbackRepository clientFeedbackRepository,RideServiceClient rideServiceClient) {
        this.clientFeedbackRepository = clientFeedbackRepository;
        this.rideServiceClient=rideServiceClient;
    }

    @Transactional
    public ClientFeedbackWithIdDTO createFeedback(ClientFeedbackDTO clientFeedbackDTO) {
        ClientFeedback clientFeedback = new ClientFeedback();
        RideWithIdDTO ride = getRide(clientFeedbackDTO.getRideId());

        if (ride == null || !ride.getStatus().equalsIgnoreCase("COMPLETED"))
        {
            throw new IllegalStateException("Can't create feedback. Ride is not completed.");
        }

        if (!ride.getUserId().equals(clientFeedbackDTO.getUserId())) {
            throw new IllegalStateException("User is not authorized to provide feedback for this ride.");
        }
        clientFeedback.setRate(clientFeedbackDTO.getRate());
        clientFeedback.setComment(clientFeedbackDTO.getComment());
        clientFeedback.setCleanInterior(clientFeedbackDTO.getCleanInterior());
        clientFeedback.setSafeDriving(clientFeedbackDTO.getSafeDriving());
        clientFeedback.setNiceMusic(clientFeedbackDTO.getNiceMusic());
        clientFeedback.setRideId(clientFeedbackDTO.getRideId());
        clientFeedback.setUserId(clientFeedbackDTO.getUserId());

        clientFeedbackRepository.save(clientFeedback);
        return clientFeedbackWithIdMapper.toDTO(clientFeedback);
    }

    public ClientFeedbackWithIdDTO getFeedback(Long id) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        clientFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));
        return clientFeedbackWithIdMapper.toDTO(clientFeedbackOptional.get());
    }

    @Transactional
    public ClientFeedbackWithIdDTO changeClientFeedback(Long id, UpdateClientRateDTO updateClientRateDTO) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        clientFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));

        ClientFeedback clientFeedback = clientFeedbackOptional.get();

        if (updateClientRateDTO.getCleanInterior() != null) {
            clientFeedback.setCleanInterior(updateClientRateDTO.getCleanInterior());
        }

        if (updateClientRateDTO.getNiceMusic() != null) {
            clientFeedback.setNiceMusic(updateClientRateDTO.getNiceMusic());
        }

        if (updateClientRateDTO.getSafeDriving() != null) {
            clientFeedback.setSafeDriving(updateClientRateDTO.getSafeDriving());
        }

        if (updateClientRateDTO.getRate() != null) {
            clientFeedback.setRate(updateClientRateDTO.getRate());
        }

        if (updateClientRateDTO.getComment() != null) {
            clientFeedback.setComment(updateClientRateDTO.getComment());
        }

        clientFeedbackRepository.save(clientFeedback);
        return clientFeedbackWithIdMapper.toDTO(clientFeedback);
    }

    @Transactional
    public void deleteFeedback(Long id) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        clientFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));
        clientFeedbackRepository.delete(clientFeedbackOptional.get());
    }

    @Transactional
    public ClientFeedbackWithIdDTO updateFeedback(Long id, ClientFeedbackDTO clientFeedbackDTO) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        clientFeedbackOptional.orElseThrow(() -> new EntityNotFoundException("Feedback from client not found"));

        ClientFeedback clientFeedback = clientFeedbackOptional.get();

        clientFeedback.setRate(clientFeedbackDTO.getRate());
        clientFeedback.setComment(clientFeedbackDTO.getComment());
        clientFeedback.setCleanInterior(clientFeedbackDTO.getCleanInterior());
        clientFeedback.setSafeDriving(clientFeedbackDTO.getSafeDriving());
        clientFeedback.setNiceMusic(clientFeedbackDTO.getNiceMusic());

        clientFeedbackRepository.save(clientFeedback);
        return clientFeedbackWithIdMapper.toDTO(clientFeedback);
    }

    public Page<ClientFeedbackWithIdDTO> getAllClientFeedbacks(Pageable pageable) {
        return clientFeedbackRepository.findAll(pageable).map(clientFeedbackWithIdMapper::toDTO);
    }

    public Page<ClientFeedbackWithIdDTO> getAllClientFeedbacksById (Long userId,Pageable pageable) {
        return clientFeedbackRepository.findAllByUserId(userId,pageable).map(clientFeedbackWithIdMapper::toDTO);
    }

    public RateDTO calculateAverageRating(Long userId) {
        List<ClientFeedback> feedbacks = clientFeedbackRepository.findByUserId(userId);

        if (feedbacks.isEmpty()) {
            return new RateDTO(0.0);
        }

        double totalRate = 0;
        double totalAttributes = 0;
        int count = 0;

        for (ClientFeedback feedback : feedbacks) {
            totalRate += feedback.getRate();
            totalAttributes += getValidAttributeValue(feedback.getCleanInterior());
            totalAttributes += getValidAttributeValue(feedback.getSafeDriving());
            totalAttributes += getValidAttributeValue(feedback.getNiceMusic());
            count++;
        }
        double averageRate = totalRate / count;
        double averageAttributes = totalAttributes / (count * 3);
        return new RateDTO((averageRate + averageAttributes) / 2);
    }

    private double getValidAttributeValue(Boolean attribute) {
        return (attribute != null && attribute) ? 1.0 : 0.0;
    }

    private RideWithIdDTO getRide(Long rideId)
    {
        return rideServiceClient.getRide(rideId);
    }
}
