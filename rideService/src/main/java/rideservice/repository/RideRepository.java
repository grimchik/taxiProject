package rideservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideservice.entity.Ride;
import rideservice.enums.Status;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {
    List<Ride> findByStatus(Status status);
    Page<Ride> findAllByUserId(Long userId, Pageable pageable);
}
