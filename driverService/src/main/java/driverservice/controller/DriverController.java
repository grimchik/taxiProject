package driverservice.controller;

import driverservice.dto.*;
import driverservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/drivers/")
public class DriverController {
    private final DriverService driverService;
    private static final Logger log = LoggerFactory.getLogger(DriverController.class);

    public DriverController(DriverService driverService)
    {
        this.driverService=driverService;
    }

    @GetMapping
    public ResponseEntity<?> getProfileById(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                            @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        log.info("GET /drivers - Getting all drivers, page={}, size={}", page, size);
        return new ResponseEntity<>(driverService.getAllDrivers(PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable("id") Long id)
    {
        log.info("GET /drivers/{} - Getting driver by ID", id);
        return new ResponseEntity<>(driverService.getDriverById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createDriver( @Valid @RequestBody DriverDTO driverDTO)
    {
        log.info("POST /drivers - Creating driver with phone: {}", driverDTO.getPhone());
        return new ResponseEntity<>(driverService.createDriver(driverDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,@Valid @RequestBody DriverDTO driverDTO)
    {
        log.info("PUT /drivers/{} - Updating driver", id);
        return new ResponseEntity<>(driverService.updateProfile(id,driverDTO),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeDriver(@PathVariable("id") Long id,@Valid @RequestBody UpdateDriverDTO updateDriverDTO)
    {
        log.info("PATCH /drivers/{} - Partially updating driver", id);
        return new ResponseEntity<>(driverService.changeDriver(id,updateDriverDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable("id") Long id)
    {
        log.info("DELETE /drivers/{} - Deleting driver", id);
        driverService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{driverId}/create-feedback")
    public ResponseEntity<?> createFeedback (@PathVariable("driverId") Long driverId,
                                             @RequestBody DriverFeedbackDTO driverFeedbackDTO)
    {
        log.info("POST /drivers/{}/create-feedback - Creating feedback", driverId);
        return new ResponseEntity<>(driverService.createFeedback(driverId,driverFeedbackDTO),HttpStatus.CREATED);
    }

    @GetMapping("/{driverId}/driver-feedbacks")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("driverId") Long driverId,
                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        log.info("GET /drivers/{}/driver-feedbacks - Getting feedbacks, page={}, size={}", driverId, page, size);
        return new ResponseEntity<>(driverService.getAllFeedbacks(driverId,page,size),HttpStatus.OK);
    }

    @GetMapping("/{driverId}/driver-rate")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("driverId") Long driverId)
    {
        log.info("GET /drivers/{}/driver-rate - Getting driver rate", driverId);
        return new ResponseEntity<>(driverService.getDriverRate(driverId),HttpStatus.OK);
    }

    @PatchMapping("/{driverId}/update-feedback/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable("driverId") Long driverId,
                                            @PathVariable("feedbackId") Long feedbackId,
                                            @RequestBody UpdateDriverRateDTO updateDriverRateDTO)
    {
        log.info("PATCH /drivers/{}/update-feedback/{} - Updating feedback", driverId, feedbackId);
        return new ResponseEntity<>(driverService.changeFeedback(driverId,feedbackId, updateDriverRateDTO), HttpStatus.OK);
    }

    @GetMapping("/available-rides")
    public ResponseEntity<?> getAvailableRides (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        log.info("GET /drivers/available-rides - Getting available rides, page={}, size={}", page, size);
        return new ResponseEntity<>(driverService.getAvailableRides(page,size),HttpStatus.OK);
    }

    @GetMapping("/{driverId}/completed-rides")
    public ResponseEntity<?> getCompletedRides (@PathVariable("driverId") Long  driverId,
                                                @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        log.info("GET /drivers/{}/completed-rides - Getting completed rides, page={}, size={}", driverId, page, size);
        return new ResponseEntity<>(driverService.getCompletedRides(driverId,page,size),HttpStatus.OK);
    }

    @PostMapping("/{driverId}/assign-car/{carId}")
    public ResponseEntity<?> assignCarToDriver(@PathVariable("driverId") Long  driverId,
                                               @PathVariable("carId") Long  carId)
    {
        log.info("POST /drivers/{}/assign-car/{} - Assigning car", driverId, carId);
        return new ResponseEntity<>(driverService.assignCarToDriver(driverId,carId),HttpStatus.OK);
    }

    @PostMapping("/{driverId}/unassign-car/{carId}")
    public ResponseEntity<?> unassignCarFromDriver(@PathVariable("driverId") Long driverId,
                                                   @PathVariable("carId") Long carId)
    {
        log.info("POST /drivers/{}/unassign-car/{} - Unassigning car", driverId, carId);
        return new ResponseEntity<>(driverService.unassignCarFromDriver(driverId, carId), HttpStatus.OK);
    }

    @PostMapping("/{driverId}/apply-ride/{rideId}")
    public ResponseEntity<?> applyRide (@PathVariable("driverId") Long driverId,
                                        @PathVariable("rideId") Long rideId)
    {
        log.info("POST /drivers/{}/apply-ride/{} - Applying to ride", driverId, rideId);
        return new ResponseEntity<>(driverService.applyRide(rideId,driverId),HttpStatus.OK);
    }

    @PostMapping("/{driverId}/cancel-ride/{rideId}")
    public ResponseEntity<?> cancelRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("driverId") Long driverId)
    {
        log.info("POST /drivers/{}/cancel-ride/{} - Cancelling ride", driverId, rideId);
        CanceledRideByDriverDTO canceledRideByDriverDTO = new CanceledRideByDriverDTO(driverId, rideId);
        driverService.cancelRide(canceledRideByDriverDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{driverId}/start-ride/{rideId}")
    public ResponseEntity<?> startRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("driverId") Long driverId)
    {
        log.info("POST /drivers/{}/start-ride/{} - Starting ride", driverId, rideId);
        RideInProgressDTO rideInProgressDTO = new RideInProgressDTO(driverId, rideId);
        driverService.startRide(rideInProgressDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{driverId}/finish-ride/{rideId}")
    public ResponseEntity<?> finishRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("driverId") Long driverId)
    {
        log.info("POST /drivers/{}/finish-ride/{} - Finishing ride", driverId, rideId);
        FinishRideDTO finishRideDTO= new FinishRideDTO(driverId, rideId);
        driverService.finishRide(finishRideDTO);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{driverId}/completed-rides-period")
    public ResponseEntity<?> getCompletedRidesPeriod(@PathVariable("driverId") Long driverId,
                                                     @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                     @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        log.info("GET /drivers/{}/completed-rides-period - From {} to {}, page={}, size={}", driverId, start, end, page, size);
        return new ResponseEntity<>(driverService.getCompletedRidesPeriod(driverId, start, end, page, size), HttpStatus.OK);
    }

    @GetMapping("/{driverId}/earning")
    public ResponseEntity<?> getEarnings(
            @PathVariable("driverId") Long driverId,
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end)
    {
        log.info("GET /drivers/{}/earning - From {} to {}", driverId, start, end);
        return new ResponseEntity<>(driverService.getEarnings(driverId, start, end), HttpStatus.OK);
    }
}