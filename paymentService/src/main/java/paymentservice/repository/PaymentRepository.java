package paymentservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paymentservice.entity.Payment;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
    Page<Payment> findPaymentsByUserId(Long userId, Pageable pageable);

    Optional<Payment> findPaymentByUserIdAndPaymentType(Long userId, String paymentType);
}
