package paymentservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.UpdatePaymentDTO;
import paymentservice.service.PaymentService;

@RestController
@RequestMapping("/api/v1/payments/")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        return new ResponseEntity<>(paymentService.createPayment(paymentDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changePayment(@PathVariable("id") Long id, @Valid @RequestBody UpdatePaymentDTO updatePaymentDTO) {
        return new ResponseEntity<>(paymentService.changePayment(id, updatePaymentDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changePayment(@PathVariable("id") Long id, @Valid @RequestBody PaymentDTO updatePaymentDTO) {
        return new ResponseEntity<>(paymentService.updatePayment(id, updatePaymentDTO), HttpStatus.OK);
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

    @GetMapping
    public ResponseEntity<?> getAllPayments (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                             @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(paymentService.getAllPayments(PageRequest.of(page, size)),HttpStatus.OK);
    }
}
