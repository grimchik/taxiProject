package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import clientservice.exception.ServiceUnavailableException;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "rideservice", url = "http://localhost:8080/api/v1/rides", configuration = FeignConfiguration.class)
public interface RideServiceClient {
    @Retry(name = "rideServiceRetry",fallbackMethod = "createRideFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "createRideFallback")
    @PostMapping("/")
    RideWithIdDTO createRide(@RequestBody RideDTO request);

    @Retry(name = "rideServiceRetry",fallbackMethod = "getRidesFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "getRidesFallback")
    @GetMapping("/user-rides/{userId}")
    Page<RideWithIdDTO> getRides(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @Retry(name = "rideServiceRetry",fallbackMethod = "changeRideFallback")
    @CircuitBreaker(name = "rideServiceCircuitBreaker", fallbackMethod = "changeRideFallback")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    RideWithIdDTO changeRide(@PathVariable("id") Long rideId,
                             @RequestBody UpdateRideDTO updateRideDTO
    );

    default RideWithIdDTO createRideFallback(RideDTO request, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot create a ride.");
    }

    default Page<RideWithIdDTO> getRidesFallback(Long userId, Integer page, Integer size, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot get rides for user ID " + userId);
    }

    default RideWithIdDTO changeRideFallback(Long rideId, UpdateRideDTO updateRideDTO, Throwable t) {
        throw new ServiceUnavailableException("Ride service is unavailable. Cannot update ride with ID " + rideId);
    }
}
