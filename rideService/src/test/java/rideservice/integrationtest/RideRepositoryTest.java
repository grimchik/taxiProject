package rideservice.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import rideservice.entity.Ride;
import rideservice.enums.Status;
import rideservice.repository.RideRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    @Test
    public void testFindByStatus() {
        Ride ride1 = new Ride();
        ride1.setStatus("COMPLETED");
        ride1.setPrice(100.0);
        ride1.setUserId(1L);
        ride1.setDriverId(10L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("COMPLETED");
        ride2.setPrice(150.0);
        ride2.setUserId(2L);
        ride2.setDriverId(11L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        Ride ride3 = new Ride();
        ride3.setStatus("CANCELED_BY_USER");
        ride3.setPrice(200.0);
        ride3.setUserId(3L);
        ride3.setDriverId(12L);
        ride3.setCreatedAt(LocalDateTime.now());
        ride3.setPromoCodeApplied(false);

        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        List<Ride> completedRides = rideRepository.findByStatus(Status.COMPLETED.name());
        assertThat(completedRides).hasSize(2);
    }

    @Test
    public void testFindAllByUserId() {
        Ride ride1 = new Ride();
        ride1.setStatus("COMPLETED");
        ride1.setPrice(100.0);
        ride1.setUserId(100L);
        ride1.setDriverId(20L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("IN_PROGRESS");
        ride2.setPrice(120.0);
        ride2.setUserId(100L);
        ride2.setDriverId(21L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        Ride ride3 = new Ride();
        ride3.setStatus("CANCELED_BY_USER");
        ride3.setPrice(130.0);
        ride3.setUserId(101L);
        ride3.setDriverId(22L);
        ride3.setCreatedAt(LocalDateTime.now());
        ride3.setPromoCodeApplied(false);

        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        Page<Ride> page = rideRepository.findAllByUserId(100L, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.getContent().forEach(ride -> assertThat(ride.getUserId()).isEqualTo(100L));
    }

    @Test
    public void testFindAllByDriverId() {
        Ride ride1 = new Ride();
        ride1.setStatus("COMPLETED");
        ride1.setPrice(110.0);
        ride1.setUserId(102L);
        ride1.setDriverId(200L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("IN_PROGRESS");
        ride2.setPrice(140.0);
        ride2.setUserId(103L);
        ride2.setDriverId(200L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        Ride ride3 = new Ride();
        ride3.setStatus("CANCELED_BY_USER");
        ride3.setPrice(160.0);
        ride3.setUserId(104L);
        ride3.setDriverId(201L);
        ride3.setCreatedAt(LocalDateTime.now());
        ride3.setPromoCodeApplied(false);

        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        Page<Ride> page = rideRepository.findAllByDriverId(200L, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.getContent().forEach(ride -> assertThat(ride.getDriverId()).isEqualTo(200L));
    }

    @Test
    public void testFindByUserIdAndStatusIn() {
        Ride ride1 = new Ride();
        ride1.setStatus("COMPLETED");
        ride1.setPrice(90.0);
        ride1.setUserId(300L);
        ride1.setDriverId(210L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("CANCELED_BY_USER");
        ride2.setPrice(95.0);
        ride2.setUserId(300L);
        ride2.setDriverId(211L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        rideRepository.save(ride1);
        rideRepository.save(ride2);

        Optional<Ride> optionalRide = rideRepository.findByUserIdAndStatusIn(300L, List.of("COMPLETED", "IN_PROGRESS"));
        assertThat(optionalRide).isPresent();
        assertThat(optionalRide.get().getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    public void testFindByStatusString() {
        Ride ride1 = new Ride();
        ride1.setStatus("CANCELED_BY_USER");
        ride1.setPrice(180.0);
        ride1.setUserId(400L);
        ride1.setDriverId(220L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("CANCELED_BY_USER");
        ride2.setPrice(190.0);
        ride2.setUserId(401L);
        ride2.setDriverId(221L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        rideRepository.save(ride1);
        rideRepository.save(ride2);

        Page<Ride> page = rideRepository.findByStatus("CANCELED_BY_USER", PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.getContent().forEach(ride -> assertThat(ride.getStatus()).isEqualTo("CANCELED_BY_USER"));
    }

    @Test
    public void testFindByStatusAndDriverId() {
        Ride ride1 = new Ride();
        ride1.setStatus("IN_PROGRESS");
        ride1.setPrice(210.0);
        ride1.setUserId(500L);
        ride1.setDriverId(300L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("IN_PROGRESS");
        ride2.setPrice(220.0);
        ride2.setUserId(501L);
        ride2.setDriverId(300L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        Ride ride3 = new Ride();
        ride3.setStatus("COMPLETED");
        ride3.setPrice(230.0);
        ride3.setUserId(502L);
        ride3.setDriverId(300L);
        ride3.setCreatedAt(LocalDateTime.now());
        ride3.setPromoCodeApplied(false);

        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        Page<Ride> page = rideRepository.findByStatusAndDriverId("IN_PROGRESS", 300L, PageRequest.of(0, 10));
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.getContent().forEach(ride -> {
            assertThat(ride.getStatus()).isEqualTo("IN_PROGRESS");
            assertThat(ride.getDriverId()).isEqualTo(300L);
        });
    }

    @Test
    public void testFindByDriverIdAndStatusIn() {
        Ride ride1 = new Ride();
        ride1.setStatus("CANCELED_BY_USER");
        ride1.setPrice(240.0);
        ride1.setUserId(600L);
        ride1.setDriverId(400L);
        ride1.setCreatedAt(LocalDateTime.now());
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("COMPLETED");
        ride2.setPrice(250.0);
        ride2.setUserId(601L);
        ride2.setDriverId(400L);
        ride2.setCreatedAt(LocalDateTime.now());
        ride2.setPromoCodeApplied(true);

        rideRepository.save(ride1);
        rideRepository.save(ride2);

        Optional<Ride> optionalRide = rideRepository.findByDriverIdAndStatusIn(400L, List.of("COMPLETED"));
        assertThat(optionalRide).isPresent();
        assertThat(optionalRide.get().getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    public void testFindByDriverIdAndStatusAndCreatedAtBetween() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        Ride ride1 = new Ride();
        ride1.setStatus("COMPLETED");
        ride1.setPrice(300.0);
        ride1.setUserId(700L);
        ride1.setDriverId(500L);
        ride1.setCreatedAt(now);
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("COMPLETED");
        ride2.setPrice(350.0);
        ride2.setUserId(701L);
        ride2.setDriverId(500L);
        ride2.setCreatedAt(now.minusHours(2));
        ride2.setPromoCodeApplied(true);

        Ride ride3 = new Ride();
        ride3.setStatus("COMPLETED");
        ride3.setPrice(400.0);
        ride3.setUserId(702L);
        ride3.setDriverId(500L);
        ride3.setCreatedAt(now.minusDays(2));
        ride3.setPromoCodeApplied(false);

        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        Page<Ride> page = rideRepository.findByDriverIdAndStatusAndCreatedAtBetween(
                500L, "COMPLETED", start, end, PageRequest.of(0, 10)
        );
        assertThat(page.getTotalElements()).isEqualTo(2);
        page.getContent().forEach(ride -> {
            assertThat(ride.getCreatedAt()).isAfterOrEqualTo(start);
            assertThat(ride.getCreatedAt()).isBeforeOrEqualTo(end);
        });
    }

    @Test
    public void testGetTotalEarnings() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(1);
        LocalDateTime end = now.plusDays(1);

        Ride ride1 = new Ride();
        ride1.setStatus("COMPLETED");
        ride1.setPrice(100.0);
        ride1.setUserId(800L);
        ride1.setDriverId(600L);
        ride1.setCreatedAt(now);
        ride1.setPromoCodeApplied(false);

        Ride ride2 = new Ride();
        ride2.setStatus("COMPLETED");
        ride2.setPrice(150.0);
        ride2.setUserId(801L);
        ride2.setDriverId(600L);
        ride2.setCreatedAt(now.minusHours(1));
        ride2.setPromoCodeApplied(true);

        Ride ride3 = new Ride();
        ride3.setStatus("CANCELED_BY_USER");
        ride3.setPrice(200.0);
        ride3.setUserId(802L);
        ride3.setDriverId(600L);
        ride3.setCreatedAt(now);
        ride3.setPromoCodeApplied(false);

        rideRepository.save(ride1);
        rideRepository.save(ride2);
        rideRepository.save(ride3);

        Double totalEarnings = rideRepository.getTotalEarnings(600L, "COMPLETED", start, end);
        assertThat(totalEarnings).isEqualTo(250.0);
    }
}
