package promocodeservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import promocodeservice.dto.*;
import promocodeservice.service.PromoCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/promocodes/")
public class PromoCodeController {

    private final PromoCodeService promoCodeService;
    private static final Logger log = LoggerFactory.getLogger(PromoCodeController.class);

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        log.info("POST /promocodes - Creating new promo code: {}", promoCodeDTO.getKeyword());
        return new ResponseEntity<>(promoCodeService.createPromoCode(promoCodeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPromoCodeById(@PathVariable("id") Long id) {
        log.info("GET /promocodes/{} - Retrieving promo code by ID", id);
        return new ResponseEntity<>(promoCodeService.getPromoCodeById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changePromoCode(@PathVariable("id") Long id, @Valid @RequestBody UpdatePromoCodeDTO updatePromoCodeDTO) {
        log.info("PATCH /promocodes/{} - Partially updating promo code", id);
        return new ResponseEntity<>(promoCodeService.changePromoCode(id, updatePromoCodeDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePromoCode(@PathVariable("id") Long id, @Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        log.info("PUT /promocodes/{} - Fully updating promo code", id);
        return new ResponseEntity<>(promoCodeService.updatePromoCode(id, promoCodeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePromoCode(@PathVariable("id") Long id) {
        log.info("DELETE /promocodes/{} - Deleting promo code", id);
        promoCodeService.deletePromoCode(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllPromoCodes (@RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /promocodes - Retrieving all promo codes, page={}, size={}", page, size);
        return new ResponseEntity<>(promoCodeService.getAllPromoCodes(PageRequest.of(page, size)), HttpStatus.OK);
    }
}