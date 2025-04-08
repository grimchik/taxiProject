package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import clientservice.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "promocodeservice", url = "http://localhost:8080/api/v1/promocodes/", configuration = FeignConfiguration.class)
public interface PromoCodeServiceClient {

    @Retry(name = "promoCodeServiceRetry",fallbackMethod = "getAllPromoCodesFallback")
    @CircuitBreaker(name = "promoCodeServiceCircuitBreaker", fallbackMethod = "getAllPromoCodesFallback")
    @GetMapping("/")
    Page<PromoCodeWithIdDTO> getAllPromoCodes(Pageable pageable);

    @Retry(name = "promoCodeServiceRetry",fallbackMethod = "createPromoCodeFallback")
    @CircuitBreaker(name = "promoCodeServiceCircuitBreaker", fallbackMethod = "createPromoCodeFallback")
    @PostMapping("/")
    PromoCodeWithIdDTO createPromoCode(@RequestBody PromoCodeDTO request);

    @Retry(name = "promoCodeServiceRetry",fallbackMethod = "changePromoCodeFallback")
    @CircuitBreaker(name = "promoCodeServiceCircuitBreaker", fallbackMethod = "changePromoCodeFallback")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    PromoCodeWithIdDTO changePromoCode(@PathVariable("id") Long promoCodeId,
                                       @RequestBody UpdatePromoCodeDTO updatePromoCodeDTO);

    @Retry(name = "promoCodeServiceRetry",fallbackMethod = "deletePromoCodeFallback")
    @CircuitBreaker(name = "promoCodeServiceCircuitBreaker", fallbackMethod = "deletePromoCodeFallback")
    @DeleteMapping("/{id}")
    void deletePromoCode(@PathVariable("id") Long promoCodeId);

    default Page<PromoCodeWithIdDTO> getAllPromoCodesFallback(Pageable pageable, Throwable t) {
        throw new ServiceUnavailableException("PromoCode service is unavailable. Cannot fetch promo codes.");
    }

    default PromoCodeWithIdDTO createPromoCodeFallback(PromoCodeDTO request, Throwable t) {
        throw new ServiceUnavailableException("PromoCode service is unavailable. Cannot create promo code.");
    }

    default PromoCodeWithIdDTO changePromoCodeFallback(Long promoCodeId, UpdatePromoCodeDTO updatePromoCodeDTO, Throwable t) {
        throw new ServiceUnavailableException("PromoCode service is unavailable. Cannot update promo code with ID " + promoCodeId);
    }

    default void deletePromoCodeFallback(Long promoCodeId, Throwable t) {
        throw new ServiceUnavailableException("PromoCode service is unavailable. Cannot delete promo code with ID " + promoCodeId);
    }

}
