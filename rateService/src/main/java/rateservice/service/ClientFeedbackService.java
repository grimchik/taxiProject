package rateservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rateservice.dto.ClientFeedbackDTO;
import rateservice.dto.ClientFeedbackWithIdDTO;
import rateservice.dto.RateDTO;
import rateservice.dto.UpdateClientRateDTO;
import rateservice.entity.ClientFeedback;
import rateservice.mapper.ClientFeedbackMapper;
import rateservice.mapper.ClientFeedbackWithIdMapper;
import rateservice.repository.ClientFeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Service
public class ClientFeedbackService {
    private final ClientFeedbackRepository clientFeedbackRepository;
    private final ClientFeedbackMapper clientFeedbackMapper = ClientFeedbackMapper.INSTANCE;
    private final ClientFeedbackWithIdMapper clientFeedbackWithIdMapper = ClientFeedbackWithIdMapper.INSTANCE;

    public ClientFeedbackService(ClientFeedbackRepository clientFeedbackRepository) {
        this.clientFeedbackRepository = clientFeedbackRepository;
    }

    @Transactional
    public ClientFeedbackWithIdDTO createFeedback(ClientFeedbackDTO clientFeedbackDTO) {
        ClientFeedback clientFeedback = new ClientFeedback();
        clientFeedback.setRate(clientFeedbackDTO.getRate());
        clientFeedback.setComment(clientFeedbackDTO.getComment());
        clientFeedback.setCleanInterior(clientFeedbackDTO.getCleanInterior());
        clientFeedback.setSafeDriving(clientFeedbackDTO.getSafeDriving());
        clientFeedback.setNiceMusic(clientFeedbackDTO.getNiceMusic());
        clientFeedbackRepository.save(clientFeedback);
        return clientFeedbackWithIdMapper.toDTO(clientFeedback);
    }

    public ClientFeedbackWithIdDTO getFeedback(Long id) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        if (clientFeedbackOptional.isEmpty()) {
            throw new EntityNotFoundException("Feedback from client not found");
        }
        return clientFeedbackWithIdMapper.toDTO(clientFeedbackOptional.get());
    }

    @Transactional
    public ClientFeedbackWithIdDTO changeClientFeedback(Long id, UpdateClientRateDTO updateClientRateDTO) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        if (clientFeedbackOptional.isEmpty()) {
            throw new EntityNotFoundException("Feedback from client not found");
        }
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
        if (clientFeedbackOptional.isEmpty()) {
            throw new EntityNotFoundException("Feedback from client not found");
        }
        clientFeedbackRepository.delete(clientFeedbackOptional.get());
    }

    @Transactional
    public ClientFeedbackWithIdDTO updateFeedback(Long id, ClientFeedbackDTO clientFeedbackDTO) {
        Optional<ClientFeedback> clientFeedbackOptional = clientFeedbackRepository.findById(id);
        if (clientFeedbackOptional.isEmpty()) {
            throw new EntityNotFoundException("Feedback from client not found");
        }
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
}
