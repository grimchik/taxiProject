package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.DriverFeedbackDTO;
import driverservice.dto.DriverFeedbackWithIdDTO;
import driverservice.dto.RateDTO;
import driverservice.dto.UpdateDriverRateDTO;
import driverservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "rateservice", url= "http://apigateway:8080/api/v1/driver-feedbacks", configuration = FeignConfiguration.class)
public interface DriverFeedbackServiceClient {
    @Retry(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "createDriverFeedbackFallback")
    @CircuitBreaker(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "createDriverFeedbackFallback")
    @RateLimiter(name = "driverFeedbackServiceRateLimiter")
    @PostMapping("/")
    DriverFeedbackWithIdDTO createDriverFeedback(@RequestBody DriverFeedbackDTO request);

    @Retry(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "getFeedbacksFallback")
    @CircuitBreaker(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "getFeedbacksFallback")
    @RateLimiter(name = "driverFeedbackServiceRateLimiter")
    @GetMapping("/driver-feedbacks/{driverId}")
    Page<DriverFeedbackWithIdDTO> getFeedbacks(@PathVariable("driverId") Long driverId,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size);

    @Retry(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "getDriverRateFallback")
    @CircuitBreaker(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "getDriverRateFallback")
    @RateLimiter(name = "driverFeedbackServiceRateLimiter")
    @GetMapping("/driver-rate/{driverId}")
    RateDTO getDriverRate(@PathVariable("driverId") Long driverId);

    @Retry(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "changeFeedBackFallback")
    @CircuitBreaker(name = "driverFeedbackServiceCircuitBreaker", fallbackMethod = "changeFeedBackFallback")
    @RateLimiter(name = "driverFeedbackServiceRateLimiter")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    DriverFeedbackWithIdDTO changeFeedBack(@PathVariable("id") Long driverFeedbackId,
                                           @RequestBody UpdateDriverRateDTO updateDriverRateDTO);

    default DriverFeedbackWithIdDTO createDriverFeedbackFallback(DriverFeedbackDTO request, Throwable t) {
        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot create driver feedback.");
    }

    default Page<DriverFeedbackWithIdDTO> getFeedbacksFallback(Long driverId, Integer page, Integer size, Throwable t) {
        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot fetch feedbacks for driver ID " + driverId);
    }

    default RateDTO getDriverRateFallback(Long driverId, Throwable t) {
        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot get rate for driver ID " + driverId);
    }

    default DriverFeedbackWithIdDTO changeFeedBackFallback(Long driverFeedbackId, UpdateDriverRateDTO updateDriverRateDTO, Throwable t) {
        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot update feedback with ID " + driverFeedbackId);
    }

}
