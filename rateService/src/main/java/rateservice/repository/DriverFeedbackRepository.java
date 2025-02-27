package rateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rateservice.entity.DriverFeedback;

@Repository
public interface DriverFeedbackRepository extends JpaRepository<DriverFeedback,Long> {
}
