package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.CarAndDriverIdDTO;
import driverservice.dto.EarningDTO;
import driverservice.dto.RideWithIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@FeignClient(name = "ride-service", url = "http://localhost:8080/api/v1/rides", configuration = FeignConfiguration.class)
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

    @PostMapping("/apply-ride/{rideId}")
    RideWithIdDTO applyRide (@PathVariable("rideId") Long rideId,
                             @RequestBody CarAndDriverIdDTO carAndDriverIdDTO);

    @GetMapping("/completed-rides-period/{driverId}")
    Page<RideWithIdDTO> getCompletedRidesPeriod(@PathVariable("driverId") Long driverId,
                                                @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "5") Integer size
    );

    @GetMapping("/earning/{driverId}")
    EarningDTO getEarnings(
            @PathVariable("driverId") Long driverId,
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    );
}
