package rateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rateservice.entity.ClientFeedback;

public interface ClientFeedbackRepository extends JpaRepository<ClientFeedback,Long> {
}
