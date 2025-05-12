package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.CarWithIdDTO;
import driverservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@FeignClient(name = "carservice",url= "http://carservice:8083/api/v1/cars", configuration = FeignConfiguration.class)
public interface CarServiceClient {
    @Retry(name = "carServiceRetry", fallbackMethod = "getCarByIdFallback")
    @CircuitBreaker(name = "carServiceCircuitBreaker", fallbackMethod = "getCarByIdFallback")
    @RateLimiter(name = "carServiceRateLimiter")
    @GetMapping("/{id}")
    CarWithIdDTO getCarById(@PathVariable("id") Long id);

    default CarWithIdDTO getCarByIdFallback(Long id, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
            int status = rse.getStatusCode().value();
            if (status == 400 || status == 404 || status == 409) {
                throw rse;
            }
        }
        throw new ServiceUnavailableException("Car service is unavailable. Cannot retrieve car with ID " + id);
    }

}
