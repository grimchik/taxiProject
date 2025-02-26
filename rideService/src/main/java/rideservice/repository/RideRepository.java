package rideservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rideservice.entity.Ride;
import rideservice.enums.Status;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {
    List<Ride> findByStatus(Status status);
}
