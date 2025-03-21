package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "client-feedbacks-service", url = "http://localhost:8080/api/v1/client-feedbacks/", configuration = FeignConfiguration.class)
public interface FeedbackServiceClient {
    @PostMapping("/")
    ClientFeedbackWithIdDTO createClientFeedback(@RequestBody ClientFeedbackDTO request);

    @GetMapping("/user-feedbacks/{userId}")
    Page<ClientFeedbackWithIdDTO> getFeedbacks (@PathVariable("userId") Long userId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @GetMapping("/user-rate/{userId}")
    RateDTO getUserRate(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    ClientFeedbackWithIdDTO changeFeedBack(@PathVariable("id") Long promoCodeId,
                                           @RequestBody UpdateClientRateDTO updateClientRateDTO
    );
}