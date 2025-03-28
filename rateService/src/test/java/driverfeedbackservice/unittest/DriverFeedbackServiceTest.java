package driverfeedbackservice.unittest;

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
import rateservice.dto.*;
import rateservice.entity.ClientFeedback;
import rateservice.entity.DriverFeedback;
import rateservice.mapper.DriverFeedbackWithIdMapper;
import rateservice.repository.ClientFeedbackRepository;
import rateservice.repository.DriverFeedbackRepository;
import rateservice.service.DriverFeedbackService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DriverFeedbackServiceTest {

    @Mock
    private DriverFeedbackRepository driverFeedbackRepository;

    @Mock
    private ClientFeedbackRepository clientFeedbackRepository;

    @Mock
    private RideServiceClient rideServiceClient;

    @Mock
    private DriverFeedbackWithIdMapper driverFeedbackWithIdMapper;

    @InjectMocks
    private DriverFeedbackService driverFeedbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFeedback_Success() {
        DriverFeedbackDTO driverFeedbackDTO = new DriverFeedbackDTO();
        driverFeedbackDTO.setRideId(1L);
        driverFeedbackDTO.setDriverId(1L);
        driverFeedbackDTO.setRate(5L);
        driverFeedbackDTO.setComment("Excellent ride!");

        RideWithIdDTO rideWithIdDTO = new RideWithIdDTO();
        rideWithIdDTO.setId(1L);
        rideWithIdDTO.setDriverId(1L);
        rideWithIdDTO.setStatus("COMPLETED");

        when(driverFeedbackRepository.findByRideIdAndDriverId(driverFeedbackDTO.getRideId(), driverFeedbackDTO.getDriverId()))
                .thenReturn(Optional.empty());
        when(rideServiceClient.getRide(1L)).thenReturn(rideWithIdDTO);

        DriverFeedback driverFeedback = new DriverFeedback();
        DriverFeedbackWithIdDTO driverFeedbackWithIdDTO = new DriverFeedbackWithIdDTO();

        when(driverFeedbackRepository.save(any(DriverFeedback.class))).thenReturn(driverFeedback);
        when(driverFeedbackWithIdMapper.toDTO(driverFeedback)).thenReturn(driverFeedbackWithIdDTO);

        DriverFeedbackWithIdDTO result = driverFeedbackService.createFeedback(driverFeedbackDTO);

        assertNotNull(result);
        verify(driverFeedbackRepository, times(1)).save(any(DriverFeedback.class));
    }

    @Test
    void testCreateFeedback_AlreadyExists() {
        DriverFeedbackDTO driverFeedbackDTO = new DriverFeedbackDTO();
        driverFeedbackDTO.setRideId(1L);
        driverFeedbackDTO.setDriverId(1L);

        when(driverFeedbackRepository.findByRideIdAndDriverId(driverFeedbackDTO.getRideId(), driverFeedbackDTO.getDriverId()))
                .thenReturn(Optional.of(new DriverFeedback()));

        assertThrows(EntityExistsException.class, () -> driverFeedbackService.createFeedback(driverFeedbackDTO));
    }

    @Test
    void testGetFeedback_Success() {
        Long feedbackId = 1L;

        DriverFeedback driverFeedback = new DriverFeedback();
        when(driverFeedbackRepository.findById(feedbackId)).thenReturn(Optional.of(driverFeedback));

        DriverFeedbackWithIdDTO driverFeedbackWithIdDTO = new DriverFeedbackWithIdDTO();
        when(driverFeedbackWithIdMapper.toDTO(driverFeedback)).thenReturn(driverFeedbackWithIdDTO);

        DriverFeedbackWithIdDTO result = driverFeedbackService.getFeedback(feedbackId);

        assertNotNull(result);
        verify(driverFeedbackRepository, times(1)).findById(feedbackId);
    }

    @Test
    void testGetFeedback_NotFound() {
        Long feedbackId = 1L;

        when(driverFeedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> driverFeedbackService.getFeedback(feedbackId));
    }

    @Test
    void testDeleteFeedback_Success() {
        Long feedbackId = 1L;

        DriverFeedback driverFeedback = new DriverFeedback();
        when(driverFeedbackRepository.findById(feedbackId)).thenReturn(Optional.of(driverFeedback));

        driverFeedbackService.deleteFeedback(feedbackId);

        verify(driverFeedbackRepository, times(1)).delete(driverFeedback);
    }

    @Test
    void testDeleteFeedback_NotFound() {
        Long feedbackId = 1L;

        when(driverFeedbackRepository.findById(feedbackId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> driverFeedbackService.deleteFeedback(feedbackId));
    }

    @Test
    void testGetAllDriverFeedbacks() {
        Page<DriverFeedback> feedbackPage = new PageImpl<>(List.of(new DriverFeedback()));
        when(driverFeedbackRepository.findAll(any(Pageable.class))).thenReturn(feedbackPage);

        Page<DriverFeedbackWithIdDTO> result = driverFeedbackService.getAllDriverFeedbacks(Pageable.unpaged());

        assertNotNull(result);
        verify(driverFeedbackRepository, times(1)).findAll(any(Pageable.class));
    }
}

