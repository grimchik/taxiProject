package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.PaymentWithIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "client-feedbacks-service", url = "http://localhost:8091/api/v1/payments/", configuration = FeignConfiguration.class)
public interface PaymentServiceClient {
    @GetMapping("/user-payments/{userId}")
    Page<PaymentWithIdDTO> getAllPaymentsByUser(@PathVariable("userId") Long userId,
                                                @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "5") Integer size);

    @GetMapping("/user-pending-payments/{userId}")
    PaymentWithIdDTO getPendingPaymentByUser(@PathVariable("userId") Long userId);
}
