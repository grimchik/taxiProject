package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.ConfirmedPaymentDTO;
import clientservice.dto.PaymentWithIdDTO;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "payments-service", url = "http://localhost:8091/api/v1/payments/", configuration = FeignConfiguration.class)
public interface PaymentServiceClient {
    @GetMapping("/user-payments/{userId}")
    Page<PaymentWithIdDTO> getAllPaymentsByUser(@PathVariable("userId") Long userId,
                                                @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "5") Integer size);

    @GetMapping("/user-pending-payments/{userId}")
    PaymentWithIdDTO getPendingPaymentByUser(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/{userId}/confirmed-payment/{paymentId}", method = RequestMethod.PATCH)
    PaymentWithIdDTO confirmedPayment(@PathVariable("userId") Long userId,
                                      @PathVariable("paymentId") Long paymentId,
                                      @Valid @RequestBody ConfirmedPaymentDTO confirmedPaymentDTO);
}
