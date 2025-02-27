package rateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rateservice.entity.DriverFeedback;

public interface DriverFeedbackRepository extends JpaRepository<DriverFeedback,Long> {
}
