package rideservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rideservice.entity.Ride;
import rideservice.enums.Status;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride,Long> {
    List<Ride> findByStatus(String status);
    Page<Ride> findAllByUserId(Long userId, Pageable pageable);
    Page<Ride> findAllByDriverId(Long driverId, Pageable pageable);
    Optional<Ride> findByUserIdAndStatusIn(Long userId, List<String> statuses);
    Page<Ride> findByStatus(String status, Pageable pageable);
    Page<Ride> findByStatusAndDriverId(String status,Long driverId, Pageable pageable);
    Optional<Ride> findByDriverIdAndStatusIn(Long driverId,List<String> statuses);
    Page<Ride> findByDriverIdAndStatusAndCreatedAtBetween(
            Long driverId, String status, LocalDateTime start, LocalDateTime end, Pageable pageable
    );
    @Query("SELECT COALESCE(SUM(r.price), 0) FROM Ride r WHERE r.driverId = :driverId AND r.status = :status AND r.createdAt BETWEEN :start AND :end")
    Double getTotalEarnings(@Param("driverId") Long driverId, @Param("status") String status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
