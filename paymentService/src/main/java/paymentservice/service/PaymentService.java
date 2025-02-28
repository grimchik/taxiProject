package paymentservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.PaymentTypeDTO;
import paymentservice.dto.PaymentWithIdDTO;
import paymentservice.dto.PriceDTO;
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
    public String changePrice(Long id, PriceDTO priceDTO)
    {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty())
        {
            throw new EntityNotFoundException("Payment not found");
        }
        Payment payment = paymentOptional.get();
        payment.setPrice(priceDTO.getPrice());
        paymentRepository.save(payment);
        return "Price changed successfully";
    }

    @Transactional
    public String changePaymentType(Long id, PaymentTypeDTO paymentTypeDTO)
    {
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty())
        {
            throw new EntityNotFoundException("Payment not found");
        }
        Payment payment = paymentOptional.get();
        payment.setPaymentType(paymentTypeDTO.getPaymentType());
        payment.setCardNumber(paymentTypeDTO.getCardNumber());
        paymentRepository.save(payment);
        return "Payment Type changed successfully";
    }
}
