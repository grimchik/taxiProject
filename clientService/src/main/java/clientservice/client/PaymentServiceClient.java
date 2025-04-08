package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.ConfirmedPaymentDTO;
import clientservice.dto.PaymentWithIdDTO;
import clientservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "paymentservice", url = "http://localhost:8080/api/v1/payments/", configuration = FeignConfiguration.class)
public interface PaymentServiceClient {

    @Retry(name = "paymentServiceRetry",fallbackMethod = "getAllPaymentsByUserFallback")
    @CircuitBreaker(name = "paymentServiceCircuitBreaker", fallbackMethod = "getAllPaymentsByUserFallback")
    @GetMapping("/user-payments/{userId}")
    Page<PaymentWithIdDTO> getAllPaymentsByUser(@PathVariable("userId") Long userId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "5") Integer size);

    @Retry(name = "paymentServiceRetry",fallbackMethod = "getPendingPaymentByUserFallback")
    @CircuitBreaker(name = "paymentServiceCircuitBreaker", fallbackMethod = "getPendingPaymentByUserFallback")
    @GetMapping("/user-pending-payments/{userId}")
    PaymentWithIdDTO getPendingPaymentByUser(@PathVariable("userId") Long userId);

    @Retry(name = "paymentServiceRetry",fallbackMethod = "confirmedPaymentFallback")
    @CircuitBreaker(name = "paymentServiceCircuitBreaker", fallbackMethod = "confirmedPaymentFallback")
    @RequestMapping(value = "/{userId}/confirmed-payment/{paymentId}", method = RequestMethod.PATCH)
    PaymentWithIdDTO confirmedPayment(@PathVariable("userId") Long userId,
                                      @PathVariable("paymentId") Long paymentId,
                                      @Valid @RequestBody ConfirmedPaymentDTO confirmedPaymentDTO);

    default Page<PaymentWithIdDTO> getAllPaymentsByUserFallback(Long userId, Integer page, Integer size, Throwable t) {
        throw new ServiceUnavailableException("Payment service is unavailable. Cannot fetch payments for user " + userId);
    }

    default PaymentWithIdDTO getPendingPaymentByUserFallback(Long userId, Throwable t) {
        throw new ServiceUnavailableException("Payment service is unavailable. Cannot get pending payment for user " + userId);
    }

    default PaymentWithIdDTO confirmedPaymentFallback(Long userId, Long paymentId, ConfirmedPaymentDTO confirmedPaymentDTO, Throwable t) {
        throw new ServiceUnavailableException("Payment service is unavailable. Cannot confirm payment with ID " + paymentId + " for user " + userId);
    }

}
