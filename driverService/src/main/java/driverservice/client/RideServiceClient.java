package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.RideWithIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ride-service", url = "http://localhost:8084/api/v1/rides", configuration = FeignConfiguration.class)
public interface RideServiceClient {
    @GetMapping("/available-rides")
    Page<RideWithIdDTO> getAvailableRides(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @GetMapping("/completed-rides/{driverId}")
    Page<RideWithIdDTO> getCompletedRides(@PathVariable("driverId") Long driverId,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @GetMapping("/active-ride/{driverId}")
    RideWithIdDTO getActiveRide(@PathVariable("driverId") Long driverId);
}
