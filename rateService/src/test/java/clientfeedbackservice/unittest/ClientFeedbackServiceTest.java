package clientfeedbackservice.unittest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import rateservice.client.RideServiceClient;
import rateservice.dto.ClientFeedbackDTO;
import rateservice.dto.ClientFeedbackWithIdDTO;
import rateservice.dto.RideWithIdDTO;
import rateservice.entity.ClientFeedback;
import rateservice.mapper.ClientFeedbackWithIdMapper;
import rateservice.repository.ClientFeedbackRepository;
import rateservice.service.ClientFeedbackService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientFeedbackServiceTest {

    @Mock
    private ClientFeedbackRepository clientFeedbackRepository;

    @Mock
    private RideServiceClient rideServiceClient;

    @Mock
    private ClientFeedbackWithIdMapper clientFeedbackWithIdMapper;

    @InjectMocks
    private ClientFeedbackService clientFeedbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFeedback_Success() {
        ClientFeedbackDTO clientFeedbackDTO = new ClientFeedbackDTO();
        clientFeedbackDTO.setRideId(1L);
        clientFeedbackDTO.setUserId(1L);
        clientFeedbackDTO.setRate(5L);
        clientFeedbackDTO.setComment("Great ride!");

        RideWithIdDTO rideWithIdDTO = new RideWithIdDTO();
        rideWithIdDTO.setId(1L);
        rideWithIdDTO.setUserId(1L);
        rideWithIdDTO.setStatus("COMPLETED");

        when(clientFeedbackRepository.findByRideIdAndUserId(clientFeedbackDTO.getRideId(), clientFeedbackDTO.getUserId()))
                .thenReturn(Optional.empty());
        when(rideServiceClient.getRide(1L)).thenReturn(rideWithIdDTO);

        ClientFeedback clientFeedback = new ClientFeedback();
        ClientFeedbackWithIdDTO savedFeedbackDTO = new ClientFeedbackWithIdDTO();

        when(clientFeedbackRepository.save(any(ClientFeedback.class))).thenReturn(clientFeedback);
        when(clientFeedbackWithIdMapper.toDTO(clientFeedback)).thenReturn(savedFeedbackDTO);

        ClientFeedbackWithIdDTO result = clientFeedbackService.createFeedback(clientFeedbackDTO);

        assertNotNull(result);
        verify(clientFeedbackRepository, times(1)).save(any(ClientFeedback.class));
    }

    @Test
    void testCreateFeedback_AlreadyExists() {
        ClientFeedbackDTO clientFeedbackDTO = new ClientFeedbackDTO();
        clientFeedbackDTO.setRideId(1L);
        clientFeedbackDTO.setUserId(1L);

        when(clientFeedbackRepository.findByRideIdAndUserId(clientFeedbackDTO.getRideId(), clientFeedbackDTO.getUserId()))
                .thenReturn(Optional.of(new ClientFeedback()));

        assertThrows(EntityExistsException.class, () -> clientFeedbackService.createFeedback(clientFeedbackDTO));
    }

    @Test
    void testGetFeedback_Success() {
        Long feedbackId = 1L;

        ClientFeedback clientFeedback = new ClientFeedback();
        when(clientFeedbackRepository.findById(feedbackId)).thenReturn(Optional.of(clientFeedback));

        ClientFeedbackWithIdDTO feedbackDTO = new ClientFeedbackWithIdDTO();
        when(clientFeedbackWithIdMapper.toDTO(clientFeedback)).thenReturn(feedbackDTO);

        ClientFeedbackWithIdDTO result = clientFeedbackService.getFeedback(feedbackId);

        assertNotNull(result);
        verify(clientFeedbackRepository, times(1)).findById(feedbackId);
    }

    @Test
    void testGetFeedback_NotFound() {
        Long feedbackId = 1L;

        when(clientFeedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientFeedbackService.getFeedback(feedbackId));
    }

    @Test
    void testDeleteFeedback_Success() {
        Long feedbackId = 1L;

        ClientFeedback clientFeedback = new ClientFeedback();
        when(clientFeedbackRepository.findById(feedbackId)).thenReturn(Optional.of(clientFeedback));

        clientFeedbackService.deleteFeedback(feedbackId);

        verify(clientFeedbackRepository, times(1)).delete(clientFeedback);
    }

    @Test
    void testDeleteFeedback_NotFound() {
        Long feedbackId = 1L;

        when(clientFeedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientFeedbackService.deleteFeedback(feedbackId));
    }

    @Test
    void testGetAllClientFeedbacks() {
        Page<ClientFeedback> feedbackPage = new PageImpl<>(List.of(new ClientFeedback()));
        when(clientFeedbackRepository.findAll(any(Pageable.class))).thenReturn(feedbackPage);

        Page<ClientFeedbackWithIdDTO> result = clientFeedbackService.getAllClientFeedbacks(Pageable.unpaged());

        assertNotNull(result);
        verify(clientFeedbackRepository, times(1)).findAll(any(Pageable.class));
    }
}

