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

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/drivers/")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService)
    {
        this.driverService=driverService;
    }

    @GetMapping
    public ResponseEntity<?> getProfileById(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                            @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverService.getAllDrivers(PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(driverService.getDriverById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createDriver( @Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.createDriver(driverDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,@Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.updateProfile(id,driverDTO),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeDriver(@PathVariable("id") Long id,@Valid @RequestBody UpdateDriverDTO updateDriverDTO)
    {
        return new ResponseEntity<>(driverService.changeDriver(id,updateDriverDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable("id") Long id)
    {
        driverService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/create-feedback")
    public ResponseEntity<?> createFeedback (@RequestBody DriverFeedbackDTO driverFeedbackDTO)
    {
        return new ResponseEntity<>(driverService.createFeedback(driverFeedbackDTO),HttpStatus.CREATED);
    }

    @GetMapping("/driver-feedbacks/{driverId}")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("driverId") Long driverId,
                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverService.getAllFeedbacks(driverId,page,size),HttpStatus.OK);
    }

    @GetMapping("/driver-rate/{driverId}")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("driverId") Long driverId)
    {
        return new ResponseEntity<>(driverService.getDriverRate(driverId),HttpStatus.OK);
    }

    @PatchMapping("/update-feedback/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable("feedbackId") Long feedbackId,
                                            @RequestBody UpdateDriverRateDTO updateDriverRateDTO)
    {
        return new ResponseEntity<>(driverService.changeFeedback(feedbackId, updateDriverRateDTO), HttpStatus.OK);
    }

    @GetMapping("/available-rides")
    public ResponseEntity<?> getAvailableRides (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverService.getAvailableRides(page,size),HttpStatus.OK);
    }

    @GetMapping("/completed-rides/{driverId}")
    public ResponseEntity<?> getCompletedRides (@PathVariable("driverId") Long  driverId,
                                                @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverService.getCompletedRides(driverId,page,size),HttpStatus.OK);
    }

    @PostMapping("{driverId}/assign-car/{carId}")
    public ResponseEntity<?> assignCarToDriver(@PathVariable("driverId") Long  driverId,
                                               @PathVariable("carId") Long  carId)
    {
        return new ResponseEntity<>(driverService.assignCarToDriver(driverId,carId),HttpStatus.OK);
    }

    @PostMapping("{driverId}/unassign-car/{carId}")
    public ResponseEntity<?> unassignCarFromDriver(@PathVariable("driverId") Long driverId,
                                                   @PathVariable("carId") Long carId) {
        return new ResponseEntity<>(driverService.unassignCarFromDriver(driverId, carId), HttpStatus.OK);
    }

    @PostMapping("{driverId}/apply-ride/{rideId}")
    public ResponseEntity<?> applyRide (@PathVariable("driverId") Long driverId,
                                        @PathVariable("rideId") Long rideId)
    {
        return new ResponseEntity<>(driverService.applyRide(rideId,driverId),HttpStatus.OK);
    }

    @PostMapping("{driverId}/cancel-ride/{rideId}")
    public ResponseEntity<?> cancelRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("driverId") Long driverId)
    {
        CanceledRideByDriverDTO canceledRideByDriverDTO = new CanceledRideByDriverDTO(driverId, rideId);
        driverService.cancelRide(canceledRideByDriverDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("{driverId}/start-ride/{rideId}")
    public ResponseEntity<?> startRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("driverId") Long driverId)
    {
        RideInProgressDTO rideInProgressDTO = new RideInProgressDTO(driverId, rideId);
        driverService.startRide(rideInProgressDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("{driverId}/finish-ride/{rideId}")
    public ResponseEntity<?> finishRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("driverId") Long driverId)
    {
        FinishRideDTO finishRideDTO= new FinishRideDTO(driverId, rideId);
        driverService.finishRide(finishRideDTO);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/completed-rides-period/{driverId}")
    public ResponseEntity<?> getCompletedRidesPeriod(@PathVariable("driverId") Long driverId,
                                                     @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                     @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverService.getCompletedRidesPeriod(driverId, start, end, page, size), HttpStatus.OK);
    }

    @GetMapping("/earning/{driverId}")
    public ResponseEntity<?> getEarnings(
            @PathVariable("driverId") Long driverId,
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end)
    {
        return new ResponseEntity<>(driverService.getEarnings(driverId, start, end), HttpStatus.OK);
    }
}