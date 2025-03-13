package paymentservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import paymentservice.dto.ConfirmedPaymentDTO;
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

    @GetMapping("/user-payments/{userId}")
    public ResponseEntity<?> getAllPaymentsByUser(@PathVariable("userId") Long userId,
                                                  @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(paymentService.getPaymentsByUser(userId,PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/user-pending-payments/{userId}")
    public ResponseEntity<?> getPendingPaymentByUser(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(paymentService.getPaymentByUserAndStatusDefault(userId),HttpStatus.OK);
    }

    @PatchMapping("/{userId}/confirmed-payment/{paymentId}")
    public ResponseEntity<?> confirmedPayment(@PathVariable("userId") Long userId,
                                              @PathVariable("paymentId") Long paymentId,
                                              @Valid @RequestBody ConfirmedPaymentDTO confirmedPaymentDTO)
    {
        return new ResponseEntity<>(paymentService.confirmedPayment(userId, paymentId, confirmedPaymentDTO),HttpStatus.OK);
    }
}
