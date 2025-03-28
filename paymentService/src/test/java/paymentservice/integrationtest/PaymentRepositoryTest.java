package paymentservice.integrationtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import paymentservice.entity.Payment;
import paymentservice.repository.PaymentRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    public void testSavePayment() {
        Payment payment = new Payment();
        payment.setPrice(49.99);
        payment.setPaymentType("CARD");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber("1234-5678-9012-3456");
        payment.setRideId(1L);
        payment.setUserId(1L);

        Payment savedPayment = paymentRepository.save(payment);
        assertThat(savedPayment.getId()).isNotNull();
    }

    @Test
    public void testFindPaymentsByUserId() {
        Payment payment1 = new Payment();
        payment1.setPrice(29.99);
        payment1.setPaymentType("CASH");
        payment1.setPaymentDate(LocalDateTime.now().minusDays(1));
        payment1.setCardNumber(null);
        payment1.setRideId(2L);
        payment1.setUserId(2L);
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setPrice(39.99);
        payment2.setPaymentType("CARD");
        payment2.setPaymentDate(LocalDateTime.now().minusDays(2));
        payment2.setCardNumber("1111-2222-3333-4444");
        payment2.setRideId(3L);
        payment2.setUserId(2L);
        paymentRepository.save(payment2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> page = paymentRepository.findPaymentsByUserId(2L, pageable);
        assertThat(page.getContent().size()).isGreaterThanOrEqualTo(2);
        assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    public void testFindPaymentByUserIdAndPaymentType() {
        Payment payment = new Payment();
        payment.setPrice(59.99);
        payment.setPaymentType("CARD");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber("2222-3333-4444-5555");
        payment.setRideId(1L);
        payment.setUserId(3L);
        paymentRepository.save(payment);

        Optional<Payment> found = paymentRepository.findPaymentByUserIdAndPaymentType(3L, "CARD");
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(3L);
        assertThat(found.get().getPaymentType()).isEqualTo("CARD");
    }

    @Test
    public void testUpdatePayment() {
        Payment payment = new Payment();
        payment.setPrice(70.00);
        payment.setPaymentType("CASH");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber(null);
        payment.setRideId(5L);
        payment.setUserId(4L);
        Payment saved = paymentRepository.save(payment);
        Long id = saved.getId();

        saved.setPrice(75.00);
        saved.setPaymentType("CARD");
        saved.setCardNumber("3333-4444-5555-6666");
        Payment updated = paymentRepository.save(saved);

        Optional<Payment> found = paymentRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getPrice()).isEqualTo(75.00);
        assertThat(found.get().getPaymentType()).isEqualTo("CARD");
        assertThat(found.get().getCardNumber()).isEqualTo("3333-4444-5555-6666");
    }

    @Test
    public void testDeletePayment() {
        Payment payment = new Payment();
        payment.setPrice(85.00);
        payment.setPaymentType("CASH");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCardNumber(null);
        payment.setRideId(6L);
        payment.setUserId(5L);
        Payment saved = paymentRepository.save(payment);
        Long id = saved.getId();
        paymentRepository.delete(saved);

        Optional<Payment> found = paymentRepository.findById(id);
        assertThat(found).isNotPresent();
    }

    @Test
    public void testFindPaymentByIdNotFoundReturnsEmpty() {
        Optional<Payment> found = paymentRepository.findById(999L);
        assertThat(found).isNotPresent();
    }

    @Test
    public void testFindPaymentByUserIdAndPaymentType_DuplicateThrowsException() {
        Payment payment1 = new Payment();
        payment1.setPrice(30.00);
        payment1.setPaymentType("CASH");
        payment1.setPaymentDate(LocalDateTime.now().minusDays(1));
        payment1.setCardNumber(null);
        payment1.setRideId(10L);
        payment1.setUserId(10L);
        paymentRepository.save(payment1);

        Payment payment2 = new Payment();
        payment2.setPrice(35.00);
        payment2.setPaymentType("CASH");
        payment2.setPaymentDate(LocalDateTime.now().minusDays(2));
        payment2.setCardNumber(null);
        payment2.setRideId(11L);
        payment2.setUserId(10L);
        paymentRepository.save(payment2);

        assertThatThrownBy(() -> paymentRepository.findPaymentByUserIdAndPaymentType(10L, "CASH"))
                .isInstanceOf(org.springframework.dao.IncorrectResultSizeDataAccessException.class);
    }
}