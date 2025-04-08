package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.CarAndDriverIdDTO;
import driverservice.dto.EarningDTO;
import driverservice.dto.RideWithIdDTO;
import driverservice.exception.ServiceUnavailableException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@FeignClient(name = "rideservice", url = "http://localhost:8080/api/v1/rides", configuration = FeignConfiguration.class)
public interface RideServiceClient {

    @Retry(name = "rideServiceRetry", fallbackMethod = "getAvailableRidesFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getAvailableRidesFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/api/v1/rides/available-rides")
    Page<RideWithIdDTO> getAvailableRides(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @Retry(name = "rideServiceRetry", fallbackMethod = "getCompletedRidesFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getCompletedRidesFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/api/v1/rides/completed-rides/{driverId}")
    Page<RideWithIdDTO> getCompletedRides(@PathVariable("driverId") Long driverId,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @Retry(name = "rideServiceRetry", fallbackMethod = "getActiveRideFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getActiveRideFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/api/v1/rides/active-ride/{driverId}")
    RideWithIdDTO getActiveRide(@PathVariable("driverId") Long driverId);

    @Retry(name = "rideServiceRetry", fallbackMethod = "applyRideFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "applyRideFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @PostMapping("/api/v1/rides/apply-ride/{rideId}")
    RideWithIdDTO applyRide (@PathVariable("rideId") Long rideId,
                             @RequestBody CarAndDriverIdDTO carAndDriverIdDTO);

    @Retry(name = "rideServiceRetry", fallbackMethod = "getCompletedRidesPeriodFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getCompletedRidesPeriodFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/api/v1/rides/completed-rides-period/{driverId}")
    Page<RideWithIdDTO> getCompletedRidesPeriod(@PathVariable("driverId") Long driverId,
                                                @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @Retry(name = "rideServiceRetry", fallbackMethod = "getEarningsFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getEarningsFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/api/v1/rides/earning/{driverId}")
    EarningDTO getEarnings(
            @PathVariable("driverId") Long driverId,
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    );

    default Page<RideWithIdDTO> getAvailableRidesFallback(Integer page, Integer size, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot fetch available rides.");
    }

    default Page<RideWithIdDTO> getCompletedRidesFallback(Long driverId, Integer page, Integer size, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot fetch completed rides for driver ID " + driverId);
    }

    default RideWithIdDTO getActiveRideFallback(Long driverId, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot fetch active ride for driver ID " + driverId);
    }

    default RideWithIdDTO applyRideFallback(Long rideId, CarAndDriverIdDTO carAndDriverIdDTO, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot apply ride with ID " + rideId);
    }

    default Page<RideWithIdDTO> getCompletedRidesPeriodFallback(Long driverId, LocalDateTime start, LocalDateTime end, Integer page, Integer size, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot fetch completed rides for driver ID " + driverId + " in the period from " + start + " to " + end);
    }

    default EarningDTO getEarningsFallback(Long driverId, LocalDateTime start, LocalDateTime end, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot fetch earnings for driver ID " + driverId + " in the period from " + start + " to " + end);
    }
}
