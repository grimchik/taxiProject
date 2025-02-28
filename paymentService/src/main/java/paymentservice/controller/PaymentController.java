package paymentservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.PaymentTypeDTO;
import paymentservice.dto.PriceDTO;
import paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/payment/")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return new ResponseEntity<>(paymentService.createPayment(paymentDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/change-price/{id}")
    public ResponseEntity<?> changePrice(@PathVariable("id") Long id, @Valid @RequestBody PriceDTO priceDTO) {
        return new ResponseEntity<>(paymentService.changePrice(id, priceDTO), HttpStatus.OK);
    }

    @PatchMapping("/change-payment-type/{id}")
    public ResponseEntity<?> changePaymentType(@PathVariable("id") Long id, @Valid @RequestBody PaymentTypeDTO paymentTypeDTO) {
        return new ResponseEntity<>(paymentService.changePaymentType(id, paymentTypeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable("id") Long id) {
        paymentService.deletePayment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable("id") Long id) {
        return new ResponseEntity<>(paymentService.getPayment(id), HttpStatus.OK);
    }
}
