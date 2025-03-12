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
public interface DriverFeedbackRepository extends JpaRepository<DriverFeedback,Long> {
    List<DriverFeedback> findByRideIdIn(List<Long> rideIds);
    Page<DriverFeedback> findAllByDriverId(Long driverId, Pageable pageable);
    Optional<DriverFeedback> findByRideIdAndDriverId(Long rideId, Long driverId);
}
