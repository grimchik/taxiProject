package paymentservice.unittest;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import paymentservice.dto.*;
import paymentservice.entity.Payment;
import paymentservice.kafkaservice.CompleteRideProducer;
import paymentservice.mapper.PaymentMapper;
import paymentservice.mapper.PaymentWithIdMapper;
import paymentservice.repository.PaymentRepository;
import paymentservice.service.PaymentService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private CompleteRideProducer completeRideProducer;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentWithIdMapper paymentWithIdMapper;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreatePayment_Success() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentType("CREDIT");
        paymentDTO.setCardNumber("123456789");
        paymentDTO.setPrice(500.0);

        Payment payment = new Payment();
        PaymentWithIdDTO paymentWithIdDTO = new PaymentWithIdDTO();

        when(paymentWithIdMapper.toDTO(any(Payment.class))).thenReturn(paymentWithIdDTO);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentWithIdDTO result = paymentService.createPayment(paymentDTO);

        assertNotNull(result);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testGetPayment_Success() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        PaymentDTO paymentDTO = new PaymentDTO();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentMapper.toDTO(payment)).thenReturn(paymentDTO);

        PaymentWithIdDTO result = paymentService.getPayment(paymentId);

        assertNotNull(result);
        verify(paymentRepository, times(1)).findById(paymentId);
    }

    @Test
    void testGetPayment_NotFound() {
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.getPayment(paymentId));
    }

    @Test
    void testDeletePayment_Success() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        paymentService.deletePayment(paymentId);

        verify(paymentRepository, times(1)).delete(payment);
    }

    @Test
    void testDeletePayment_NotFound() {
        Long paymentId = 1L;

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> paymentService.deletePayment(paymentId));
    }

    @Test
    void testUpdatePayment_Success() {
        Long paymentId = 1L;
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentType("DEBIT");
        paymentDTO.setCardNumber("987654321");
        paymentDTO.setPrice(200.0);

        Payment payment = new Payment();
        PaymentWithIdDTO paymentWithIdDTO = new PaymentWithIdDTO();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentWithIdMapper.toDTO(payment)).thenReturn(paymentWithIdDTO);

        PaymentWithIdDTO result = paymentService.updatePayment(paymentId, paymentDTO);

        assertNotNull(result);
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testConfirmedPayment_Success() {
        Long userId = 1L;
        Long paymentId = 1L;
        ConfirmedPaymentDTO confirmedPaymentDTO = new ConfirmedPaymentDTO();
        confirmedPaymentDTO.setPaymentType("CREDIT");
        confirmedPaymentDTO.setCardNumber("123456789");
        confirmedPaymentDTO.setPrice(500.0);

        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setPrice(500.0);
        payment.setPaymentType("DEFAULT");

        PaymentWithIdDTO paymentWithIdDTO = new PaymentWithIdDTO();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(paymentWithIdMapper.toDTO(payment)).thenReturn(paymentWithIdDTO);

        PaymentWithIdDTO result = paymentService.confirmedPayment(userId, paymentId, confirmedPaymentDTO);

        assertNotNull(result);
        verify(paymentRepository, times(1)).save(payment);
        verify(completeRideProducer, times(1)).sendCancelRequest(any(CompleteRideDTO.class));
    }
}