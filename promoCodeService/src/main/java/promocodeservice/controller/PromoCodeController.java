package promocodeservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import promocodeservice.dto.*;
import promocodeservice.service.PromoCodeService;

@RestController
@RequestMapping("/api/promocode/")
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @PostMapping("/create-promocode")
    public ResponseEntity<?> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        return new ResponseEntity<>(promoCodeService.createPromoCode(promoCodeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/get-promocode")
    public ResponseEntity<?> getPromoCodeByKeyword(@Valid @RequestBody KeywordDTO keywordDTO) {
        return new ResponseEntity<>(promoCodeService.getPromoCodeByKeyword(keywordDTO), HttpStatus.OK);
    }
    @GetMapping("/get-promocode/{id}")
    public ResponseEntity<?> getPromoCodeById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(promoCodeService.getPromoCodeById(id), HttpStatus.OK);
    }

    @PatchMapping("/change-keyword/{id}")
    public ResponseEntity<?> changeKeyword(@PathVariable Long id, @Valid @RequestBody KeywordDTO keywordDTO) {
        return new ResponseEntity<>(promoCodeService.changeKeyWord(id, keywordDTO), HttpStatus.OK);
    }

    @PatchMapping("/change-activation-date/{id}")
    public ResponseEntity<?> changeActivationDate(@PathVariable Long id, @Valid @RequestBody ActivationDateDTO activationDateDTO) {
        return new ResponseEntity<>(promoCodeService.changeActivationDate(id, activationDateDTO), HttpStatus.OK);
    }

    @PatchMapping("/change-expiry-date/{id}")
    public ResponseEntity<?> changeExpiryDate(@PathVariable Long id, @Valid @RequestBody ExpiryDateDTO expiryDateDTO) {
        return new ResponseEntity<>(promoCodeService.changeExpiryDate(id, expiryDateDTO), HttpStatus.OK);
    }

    @PatchMapping("/change-percent/{id}")
    public ResponseEntity<?> changePercent(@PathVariable Long id, @Valid @RequestBody PercentDTO percentDTO) {
        return new ResponseEntity<>(promoCodeService.changePercent(id, percentDTO), HttpStatus.OK);
    }

    @PutMapping("/update-promocode/{id}")
    public ResponseEntity<?> updatePromoCode(@PathVariable Long id, @Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        return new ResponseEntity<>(promoCodeService.updatePromoCode(id, promoCodeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete-promocode/{id}")
    public ResponseEntity<?> deletePromoCode(@PathVariable Long id) {
        promoCodeService.deletePromoCode(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
