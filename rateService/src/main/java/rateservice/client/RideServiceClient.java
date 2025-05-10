package rateservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rateservice.configuration.FeignConfiguration;
import rateservice.dto.RideWithIdDTO;
import org.springframework.web.server.ResponseStatusException;
import rateservice.exception.ServiceUnavailableException;

@FeignClient(name = "rideservice", url = "http://rideservice:8084/api/v1/rides", configuration = FeignConfiguration.class)
public interface RideServiceClient {

    @Retry(name = "rideServiceRetry", fallbackMethod = "getRideFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getRideFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/{id}")
    RideWithIdDTO getRide(@PathVariable("id") Long rideId);

    @Retry(name = "rideServiceRetry", fallbackMethod = "getRidesFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getRidesFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/user-rides/{userId}")
    Page<RideWithIdDTO> getRides(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @Retry(name = "rideServiceRetry", fallbackMethod = "getRidesByDriverFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getRidesByDriverFallback")
    @RateLimiter(name = "rideServiceRateLimiter")
    @GetMapping("/driver-rides/{driverId}")
    Page<RideWithIdDTO> getRidesByDriverId(
            @PathVariable("driverId") Long driverId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );


    default RideWithIdDTO getRideFallback(Long rideId, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
            int status = rse.getStatusCode().value();
            if (status == 400 || status == 404 || status == 409) {
                throw rse;
            }
        }
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot get ride with ID " + rideId);
    }

    default Page<RideWithIdDTO> getRidesFallback(Long userId, Integer page, Integer size, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
            int status = rse.getStatusCode().value();
            if (status == 400 || status == 404 || status == 409) {
                throw rse;
            }
        }
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot get rides for user ID " + userId);
    }

    default Page<RideWithIdDTO> getRidesByDriverFallback(Long driverId, Integer page, Integer size, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
            int status = rse.getStatusCode().value();
            if (status == 400 || status == 404 || status == 409) {
                throw rse;
            }
        }
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot get rides for driver ID " + driverId);
    }
}
