package rateservice.client;

import rateservice.configuration.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import rateservice.dto.RideWithIdDTO;

@FeignClient(name = "ride-service", url = "http://localhost:8084/api/v1/rides", configuration = FeignConfiguration.class)
public interface RideServiceClient {

    @GetMapping("/{id}")
    RideWithIdDTO getRide(
            @PathVariable("id") Long rideId
    );

    @GetMapping("/user-rides/{userId}")
    Page<RideWithIdDTO> getRides(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @GetMapping("/driver-rides/{driverId}")
    Page<RideWithIdDTO> getRidesByDriverId(
            @PathVariable("driverId") Long driverId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );
}