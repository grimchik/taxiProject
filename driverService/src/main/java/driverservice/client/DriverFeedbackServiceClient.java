package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.DriverFeedbackDTO;
import driverservice.dto.DriverFeedbackWithIdDTO;
import driverservice.dto.RateDTO;
import driverservice.dto.UpdateDriverRateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "driver-feedbacks-service", url = "http://localhost:8080/api/v1/driver-feedbacks/", configuration = FeignConfiguration.class)
public interface DriverFeedbackServiceClient {
    @PostMapping("/")
    DriverFeedbackWithIdDTO createDriverFeedback(@RequestBody DriverFeedbackDTO request);

    @GetMapping("/driver-feedbacks/{driverId}")
    Page<DriverFeedbackWithIdDTO> getFeedbacks (@PathVariable("driverId") Long driverId,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @GetMapping("/driver-rate/{driverId}")
    RateDTO getDriverRate(@PathVariable("driverId") Long driverId);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    DriverFeedbackWithIdDTO changeFeedBack(@PathVariable("id") Long driverFeedbackId,
                                           @RequestBody UpdateDriverRateDTO updateDriverRateDTO
    );
}
