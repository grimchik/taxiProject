package rateservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rateservice.entity.ClientFeedback;
import rateservice.entity.DriverFeedback;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientFeedbackRepository extends JpaRepository<ClientFeedback,Long> {
    Page<ClientFeedback> findAllByUserId(Long userId, Pageable pageable);
    List<ClientFeedback> findByRideIdIn(List<Long> rideIds);
    Optional<ClientFeedback> findByRideIdAndUserId(Long rideId, Long userId);
}
