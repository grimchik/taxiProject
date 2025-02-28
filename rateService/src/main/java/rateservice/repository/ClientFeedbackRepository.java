package rateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rateservice.entity.ClientFeedback;

@Repository
public interface ClientFeedbackRepository extends JpaRepository<ClientFeedback,Long> {
}
