package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "promocode-service", url = "http://localhost:8080/api/v1/promocodes/", configuration = FeignConfiguration.class)
public interface PromoCodeServiceClient {
    @GetMapping("/")
    Page<PromoCodeWithIdDTO> getAllPromoCodes (Pageable pageable);

    @PostMapping("/")
    PromoCodeWithIdDTO createPromoCode(@RequestBody PromoCodeDTO request);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    PromoCodeWithIdDTO changePromoCode(@PathVariable("id") Long promoCodeId,
                                       @RequestBody UpdatePromoCodeDTO updatePromoCodeDTO
    );

    @DeleteMapping("/{id}")
    void deletePromoCode(@PathVariable("id") Long promoCodeId);
}
