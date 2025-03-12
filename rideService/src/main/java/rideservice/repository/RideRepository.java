package rideservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideservice.entity.Ride;
import rideservice.enums.Status;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {
    List<Ride> findByStatus(Status status);
    Page<Ride> findAllByUserId(Long userId, Pageable pageable);
    Page<Ride> findAllByDriverId(Long driverId, Pageable pageable);
    Optional<Ride> findByUserIdAndStatusIn(Long userId, List<String> statuses);
    Page<Ride> findByStatus(String status, Pageable pageable);
    Page<Ride> findByStatusAndDriverId(String status,Long driverId, Pageable pageable);
    Optional<Ride> findByDriverIdAndStatusIn(Long driverId,List<String> statuses);
}
