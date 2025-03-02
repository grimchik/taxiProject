package paymentservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.PaymentWithIdDTO;
import paymentservice.dto.UpdatePaymentDTO;
import paymentservice.entity.Payment;
import paymentservice.mapper.PaymentMapper;
import paymentservice.mapper.PaymentWithIdMapper;
import paymentservice.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper=PaymentMapper.INSTANCE;
    private final PaymentWithIdMapper paymentWithIdMapper = PaymentWithIdMapper.INSTANCE;

    public PaymentService (PaymentRepository paymentRepository)
    {
        this.paymentRepository=paymentRepository;
    }

    @Transactional
    public PaymentWithIdDTO createPayment(PaymentDTO paymentDTO)
    {
        Payment payment = new Payment();
        payment.setPaymentType(paymentDTO.getPaymentType());
        payment.setCardNumber(paymentDTO.getCardNumber());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPrice(paymentDTO.getPrice());
        paymentRepository.save(payment);
        return paymentWithIdMapper.toDTO(payment);
    }

    public PaymentDTO getPayment(Long id)
    {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty())
        {
            throw new EntityNotFoundException("Payment not found");
        }
        return paymentMapper.toDTO(paymentOptional.get());
    }

    @Transactional
    public void deletePayment (Long id)
    {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty())
        {
            throw new EntityNotFoundException("Payment not found");
        }
        paymentRepository.delete(paymentOptional.get());
    }

    @Transactional
    public PaymentWithIdDTO updatePayment(Long id,PaymentDTO paymentDTO)
    {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty())
        {
            throw new EntityNotFoundException("Payment not found");
        }
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
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty())
        {
            throw new EntityNotFoundException("Payment not found");
        }
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
        return paymentRepository.findAll(pageable).map(paymentWithIdMapper::toDTO);
    }
}
