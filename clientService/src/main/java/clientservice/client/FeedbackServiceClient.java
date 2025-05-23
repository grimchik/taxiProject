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
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@FeignClient(name = "rateservice", url= "http://rateservice:8085/api/v1/client-feedbacks", configuration = FeignConfiguration.class)
public interface FeedbackServiceClient {
    @Retry(name = "feedbackServiceRetry", fallbackMethod = "createClientFeedbackFallback")
    @CircuitBreaker(name = "feedbackServiceCircuitBreaker", fallbackMethod = "createClientFeedbackFallback")
    @RateLimiter(name = "feedbackServiceRateLimiter")
    @PostMapping("/")
    ClientFeedbackWithIdDTO createClientFeedback(@RequestBody ClientFeedbackDTO request);

    @Retry(name = "feedbackServiceRetry",fallbackMethod = "getFeedbacksFallback")
    @CircuitBreaker(name = "feedbackServiceCircuitBreaker", fallbackMethod = "getFeedbacksFallback")
    @RateLimiter(name = "feedbackServiceRateLimiter")
    @GetMapping("/user-feedbacks/{userId}")
    Page<ClientFeedbackWithIdDTO> getFeedbacks(@PathVariable("userId") Long userId,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size);

    @Retry(name = "feedbackServiceRetry",fallbackMethod = "getUserRateFallback")
    @CircuitBreaker(name = "feedbackServiceCircuitBreaker", fallbackMethod = "getUserRateFallback")
    @RateLimiter(name = "feedbackServiceRateLimiter")
    @GetMapping("/user-rate/{userId}")
    RateDTO getUserRate(@PathVariable("userId") Long userId);

    @Retry(name = "feedbackServiceRetry",fallbackMethod = "changeFeedBackFallback")
    @CircuitBreaker(name = "feedbackServiceCircuitBreaker", fallbackMethod = "changeFeedBackFallback")
    @RateLimiter(name = "feedbackServiceRateLimiter")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    ClientFeedbackWithIdDTO changeFeedBack(@PathVariable("id") Long promoCodeId,
                                           @RequestBody UpdateClientRateDTO updateClientRateDTO);

    default ClientFeedbackWithIdDTO createClientFeedbackFallback(ClientFeedbackDTO request, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
    int status = rse.getStatusCode().value();
    if (status == 400 || status == 404 || status == 409) {
        throw rse;
    }
}

        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot create feedback.");
    }

    default Page<ClientFeedbackWithIdDTO> getFeedbacksFallback(Long userId, Integer page, Integer size, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
    int status = rse.getStatusCode().value();
    if (status == 400 || status == 404 || status == 409) {
        throw rse;
    }
}

        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot get feedbacks.");
    }

    default RateDTO getUserRateFallback(Long userId, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
    int status = rse.getStatusCode().value();
    if (status == 400 || status == 404 || status == 409) {
        throw rse;
    }
}

        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot get user rate.");
    }

    default ClientFeedbackWithIdDTO changeFeedBackFallback(Long promoCodeId, UpdateClientRateDTO updateClientRateDTO, Throwable t) {
        if (t instanceof ResponseStatusException rse) {
    int status = rse.getStatusCode().value();
    if (status == 400 || status == 404 || status == 409) {
        throw rse;
    }
}

        throw new ServiceUnavailableException("Feedback service is unavailable. Cannot update feedback.");
    }

}
