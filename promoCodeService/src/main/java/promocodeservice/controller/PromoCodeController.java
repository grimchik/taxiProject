package promocodeservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import promocodeservice.dto.*;
import promocodeservice.service.PromoCodeService;

@RestController
@RequestMapping("/api/v1/promocodes/")
public class PromoCodeController {
    private final PromoCodeService promoCodeService;

    public PromoCodeController(PromoCodeService promoCodeService) {
        this.promoCodeService = promoCodeService;
    }

    @PostMapping
    public ResponseEntity<?> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        return new ResponseEntity<>(promoCodeService.createPromoCode(promoCodeDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPromoCodeById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(promoCodeService.getPromoCodeById(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changePromoCode(@PathVariable("id") Long id, @Valid @RequestBody UpdatePromoCodeDTO updatePromoCodeDTO) {
        return new ResponseEntity<>(promoCodeService.changePromoCode(id, updatePromoCodeDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromoCode(@PathVariable("id") Long id, @Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        return new ResponseEntity<>(promoCodeService.updatePromoCode(id, promoCodeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromoCode(@PathVariable("id") Long id) {
        promoCodeService.deletePromoCode(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> getAllPromoCodes (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                               @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(promoCodeService.getAllPromoCodes(PageRequest.of(page, size)),HttpStatus.OK);
    }
}
