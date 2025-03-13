package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ride-service", url = "http://localhost:8080/api/v1/rides", configuration = FeignConfiguration.class)
public interface RideServiceClient {

    @PostMapping("/")
    RideWithIdDTO createRide(@RequestBody RideDTO request);

    @GetMapping("/user-rides/{userId}")
    Page<RideWithIdDTO> getRides(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    RideWithIdDTO changeRide(@PathVariable("id") Long rideId,
                             @RequestBody UpdateRideDTO updateRideDTO
    );
}