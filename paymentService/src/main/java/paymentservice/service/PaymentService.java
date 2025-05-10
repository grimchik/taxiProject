package paymentservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentservice.dto.*;
import paymentservice.entity.Payment;
import paymentservice.kafkaservice.CompleteRideProducer;
import paymentservice.kafkaservice.CreatePaymentConsumer;
import paymentservice.mapper.PaymentMapper;
import paymentservice.mapper.PaymentWithIdMapper;
import paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper=PaymentMapper.INSTANCE;
    private final PaymentWithIdMapper paymentWithIdMapper = PaymentWithIdMapper.INSTANCE;

    private final CompleteRideProducer completeRideProducer;

    public PaymentService (PaymentRepository paymentRepository,CompleteRideProducer completeRideProducer)
    {
        this.paymentRepository=paymentRepository;
        this.completeRideProducer=completeRideProducer;
    }

    @Transactional
    public PaymentWithIdDTO createPayment(PaymentDTO paymentDTO)
    {
        Payment payment = new Payment();
        payment.setPaymentType(paymentDTO.getPaymentType());
        payment.setCardNumber(paymentDTO.getCardNumber());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPrice(paymentDTO.getPrice());
        payment.setRideId(paymentDTO.getRideId());
        payment.setUserId(paymentDTO.getUserId());
        paymentRepository.save(payment);
        log.info("Payment created for userId={}, rideId={}", payment.getUserId(), payment.getRideId());
        return paymentWithIdMapper.toDTO(payment);
    }

    public PaymentWithIdDTO getPayment(Long id)
    {
        log.info("Payment with id={} retrieved", id);

        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        paymentOptional.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return paymentWithIdMapper.toDTO(paymentOptional.get());
    }

    @Transactional
    public void deletePayment (Long id)
    {
        log.info("Payment with id={} deleted", id);

        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        paymentOptional.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        paymentRepository.delete(paymentOptional.get());
    }

    @Transactional
    public PaymentWithIdDTO updatePayment(Long id,PaymentDTO paymentDTO)
    {
        log.info("Payment with id={} updated", id);

        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        paymentOptional.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        Payment payment = paymentOptional.get();
        payment.setPaymentType(paymentDTO.getPaymentType());
        payment.setCardNumber(paymentDTO.getCardNumber());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPrice(paymentDTO.getPrice());
        paymentRepository.save(payment);
        return paymentWithIdMapper.toDTO(payment);
    }

    @Transactional
    public PaymentWithIdDTO changePayment(Long id, UpdatePaymentDTO updatePaymentDTO)
    {
        log.info("Payment with id={} partially updated", id);

        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        paymentOptional.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        Payment payment = paymentOptional.get();
        if(updatePaymentDTO.getPaymentType() != null)
        {
            payment.setPaymentType(updatePaymentDTO.getPaymentType());
        }

        if (updatePaymentDTO.getPrice() != null)
        {
            payment.setPrice(updatePaymentDTO.getPrice());
        }

        if (updatePaymentDTO.getCardNumber() != null)
        {
            payment.setCardNumber(updatePaymentDTO.getCardNumber());
        }

        paymentRepository.save(payment);
        return paymentWithIdMapper.toDTO(payment);
    }

    public Page<PaymentWithIdDTO> getAllPayments (Pageable pageable)
    {
        log.info("Retrieving all payments with pagination");

        return paymentRepository.findAll(pageable).map(paymentWithIdMapper::toDTO);
    }

    @Transactional
    public void createPaymentByRide(CreatePaymentDTO message)
    {
        log.info("Payment created by ride for userId={}, rideId={}", message.getUserId(), message.getRideId());

        Payment payment = new Payment();
        payment.setRideId(message.getRideId());
        payment.setUserId(message.getUserId());
        payment.setPrice(message.getPrice());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentType("DEFAULT");
        paymentRepository.save(payment);
    }

    public Page<PaymentWithIdDTO> getPaymentsByUser(Long userId,Pageable pageable)
    {
        log.info("Retrieving payments for userId={}", userId);

        return paymentRepository.findPaymentsByUserId(userId,pageable).map(paymentWithIdMapper::toDTO);
    }

    public PaymentWithIdDTO getPaymentByUserAndStatusDefault(Long userId)
    {
        log.info("Default payment retrieved for userId={}", userId);

        Optional<Payment> paymentOptional = paymentRepository.findPaymentByUserIdAndPaymentType(userId,"DEFAULT");
        paymentOptional.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return paymentWithIdMapper.toDTO(paymentOptional.get());
    }

    @Transactional
    public PaymentWithIdDTO confirmedPayment(Long userId, Long paymentId, ConfirmedPaymentDTO confirmedPaymentDTO)
    {
        log.info("Payment with id={} confirmed by userId={}", paymentId, userId);
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        paymentOptional.orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        Payment payment = paymentOptional.get();
        if(!payment.getPaymentType().equals("DEFAULT"))
        {
            throw new IllegalStateException("This payment already paid");
        }
        if (!payment.getUserId().equals(userId))
        {
            throw new IllegalStateException("");
        }

        if (!payment.getPrice().equals(confirmedPaymentDTO.getPrice()))
        {
            throw new IllegalStateException("");
        }

        payment.setPaymentType(confirmedPaymentDTO.getPaymentType());
        payment.setCardNumber(confirmedPaymentDTO.getCardNumber());
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);
        completeRideProducer.sendCancelRequest(new CompleteRideDTO(payment.getRideId()));
        return paymentWithIdMapper.toDTO(payment);
    }
}
