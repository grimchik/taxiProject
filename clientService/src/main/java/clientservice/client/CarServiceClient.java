package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import clientservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "carservice", url = "http://localhost:8080/api/v1/cars",configuration = FeignConfiguration.class)
public interface CarServiceClient {
    @Retry(name = "carServiceRetry", fallbackMethod = "getAllCarsFallback")
    @CircuitBreaker(name = "carServiceCircuitBreaker", fallbackMethod = "getAllCarsFallback")
    @RateLimiter(name = "carServiceRateLimiter")
    @GetMapping("/api/v1/cars/")
    Page<CarWithIdDTO> getAllCars(Pageable pageable);

    @Retry(name = "carServiceRetry", fallbackMethod = "createCarFallback")
    @CircuitBreaker(name = "carServiceCircuitBreaker", fallbackMethod = "createCarFallback")
    @RateLimiter(name = "carServiceRateLimiter")
    @PostMapping("/api/v1/cars/")
    CarWithIdDTO createCar(@RequestBody CarCreateDTO request);

    @Retry(name = "carServiceRetry", fallbackMethod = "changeCarFallback")
    @CircuitBreaker(name = "carServiceCircuitBreaker", fallbackMethod = "changeCarFallback")
    @RateLimiter(name = "carServiceRateLimiter")
    @RequestMapping(value = "/api/v1/cars/{id}", method = RequestMethod.PATCH)
    CarWithIdDTO changeCar(@PathVariable("id") Long carId, @RequestBody UpdateCarDTO updateCarDTO);

    @Retry(name = "carServiceRetry", fallbackMethod = "deleteCarFallback")
    @CircuitBreaker(name = "carServiceCircuitBreaker", fallbackMethod = "deleteCarFallback")
    @RateLimiter(name = "carServiceRateLimiter")
    @DeleteMapping("/api/v1/cars/{id}")
    void deleteCar(@PathVariable("id") Long carId);

    default Page<CarWithIdDTO> getAllCarsFallback(Pageable pageable, Throwable t) {
        throw new ServiceUnavailableException("Car service is currently unavailable");
    }

    default CarWithIdDTO createCarFallback(CarCreateDTO request, Throwable t) {
        throw new ServiceUnavailableException("Car service is currently unavailable. Cannot create car");
    }

    default CarWithIdDTO changeCarFallback(Long carId, UpdateCarDTO updateCarDTO, Throwable t) {
        throw new ServiceUnavailableException("Car service is currently unavailable. Cannot update car");
    }

    default void deleteCarFallback(Long carId, Throwable t) {
        throw new ServiceUnavailableException("Car service is currently unavailable. Cannot delete car");
    }
}


