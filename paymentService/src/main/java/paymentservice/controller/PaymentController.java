package paymentservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import paymentservice.dto.ConfirmedPaymentDTO;
import paymentservice.dto.PaymentDTO;
import paymentservice.dto.UpdatePaymentDTO;
import paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/payments/")
public class PaymentController {
    private final PaymentService paymentService;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("POST /payments - Creating new payment for user ID: {}, ride ID: {}", paymentDTO.getUserId(), paymentDTO.getRideId());

        return new ResponseEntity<>(paymentService.createPayment(paymentDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePayment(@PathVariable("id") Long id, @Valid @RequestBody UpdatePaymentDTO updatePaymentDTO) {
        log.info("PATCH /payments - Patching payment with ID: {}", id);

        return new ResponseEntity<>(paymentService.changePayment(id, updatePaymentDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePayment(@PathVariable("id") Long id, @Valid @RequestBody PaymentDTO updatePaymentDTO) {
        log.info("PUT /payments - Fully updating payment with ID: {}", id);

        return new ResponseEntity<>(paymentService.updatePayment(id, updatePaymentDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePayment(@PathVariable("id") Long id) {
        log.info("DELETE /payments - Deleting payment with ID: {}", id);
        paymentService.deletePayment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPayment(@PathVariable("id") Long id) {
        log.info("GET /payments - Getting payment with ID: {}", id);
        return new ResponseEntity<>(paymentService.getPayment(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllPayments (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                             @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        log.info("GET /payments - Getting all payments, page={}, size={}", page, size);

        return new ResponseEntity<>(paymentService.getAllPayments(PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/user-payments/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllPaymentsByUser(@PathVariable("userId") Long userId,
                                                  @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        log.info("GET /payments/user-payments - Getting payments for user ID: {}, page={}, size={}", userId, page, size);

        return new ResponseEntity<>(paymentService.getPaymentsByUser(userId,PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/user-pending-payments/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getPendingPaymentByUser(@PathVariable("userId") Long userId)
    {
        log.info("GET /payments/user-pending-payments - Getting pending payments for user ID: {}", userId);

        return new ResponseEntity<>(paymentService.getPaymentByUserAndStatusDefault(userId),HttpStatus.OK);
    }

    @PatchMapping("/{userId}/confirmed-payment/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> confirmedPayment(@PathVariable("userId") Long userId,
                                              @PathVariable("paymentId") Long paymentId,
                                              @Valid @RequestBody ConfirmedPaymentDTO confirmedPaymentDTO)
    {
        log.info("PATCH /payments/confirmed-payment - Confirming payment ID: {} for user ID: {}", paymentId, userId);

        return new ResponseEntity<>(paymentService.confirmedPayment(userId, paymentId, confirmedPaymentDTO),HttpStatus.OK);
    }
}
