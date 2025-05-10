package rideservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rideservice.dto.CarAndDriverIdDTO;
import rideservice.dto.RideDTO;
import rideservice.dto.UpdateRideDTO;
import rideservice.service.RideService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/rides/")
public class RideController {
    private final RideService rideService;
    private static final Logger log = LoggerFactory.getLogger(RideController.class);

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<?> createRide(@Valid @RequestBody RideDTO rideDTO) {
        log.info("POST /rides - Creating new ride from user ID: {}", rideDTO.getUserId());
        return new ResponseEntity<>(rideService.createRide(rideDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeRide(@PathVariable("id") Long id, @Valid @RequestBody UpdateRideDTO updateRideDTO) {
        log.info("PATCH /rides/{} - Updating ride status", id);
        return new ResponseEntity<>(rideService.changeRide(id, updateRideDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable("id") Long id) {
        log.info("DELETE /rides/{} - Deleting ride", id);
        rideService.deleteRideById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRideById(@PathVariable("id") Long id) {
        log.info("GET /rides/{} - Retrieving ride by ID", id);
        return new ResponseEntity<>(rideService.getRideById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllRides(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /rides - Retrieving all rides, page={}, size={}", page, size);
        return new ResponseEntity<>(rideService.getAllRides(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/user-rides/{userId}")
    public ResponseEntity<?> getAllRidesByUserId(@PathVariable("userId") Long userId,
                                                 @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                 @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /rides/user-rides/{} - Retrieving rides for user, page={}, size={}", userId, page, size);
        return new ResponseEntity<>(rideService.getAllRidesByUserId(userId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/driver-rides/{driverId}")
    public ResponseEntity<?> getAllRidesByDriverId(@PathVariable("driverId") Long driverId,
                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /rides/driver-rides/{} - Retrieving rides for driver, page={}, size={}", driverId, page, size);
        return new ResponseEntity<>(rideService.getAllRidesByDriverId(driverId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/available-rides")
    public ResponseEntity<?> getAllAvailableRides(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /rides/available-rides - Retrieving all available rides, page={}, size={}", page, size);
        return new ResponseEntity<>(rideService.getAllAvailableRides(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/completed-rides/{driverId}")
    public ResponseEntity<?> getCompletedRides(@PathVariable("driverId") Long driverId,
                                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                                               @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /rides/completed-rides/{} - Retrieving completed rides for driver, page={}, size={}", driverId, page, size);
        return new ResponseEntity<>(rideService.getCompletedRides(driverId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/active-ride/{driverId}")
    public ResponseEntity<?> getActiveRide(@PathVariable("driverId") Long driverId) {
        log.info("GET /rides/active-ride/{} - Retrieving active ride for driver", driverId);
        return new ResponseEntity<>(rideService.getActiveRide(driverId), HttpStatus.OK);
    }

    @PostMapping("/apply-ride/{rideId}")
    public ResponseEntity<?> applyRide(@PathVariable("rideId") Long rideId,
                                       @Valid @RequestBody CarAndDriverIdDTO carAndDriverIdDTO) {
        log.info("POST /rides/apply-ride/{} - Driver ID {} applying with car ID {}", rideId, carAndDriverIdDTO.getDriverId(), carAndDriverIdDTO.getCarId());
        return new ResponseEntity<>(rideService.applyRide(rideId, carAndDriverIdDTO), HttpStatus.OK);
    }

    @GetMapping("/completed-rides-period/{driverId}")
    public ResponseEntity<?> getCompletedRidesPeriod(@PathVariable("driverId") Long driverId,
                                                     @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                     @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /rides/completed-rides-period/{} - From: {}, To: {}, page={}, size={}", driverId, start, end, page, size);
        return new ResponseEntity<>(rideService.getCompletedRidesPeriod(driverId, start, end, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/earning/{driverId}")
    public ResponseEntity<?> getCompletedRidesPeriod(@PathVariable("driverId") Long driverId,
                                                     @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                     @RequestParam(value = "end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("GET /rides/earning/{} - Calculating earnings from {} to {}", driverId, start, end);
        return new ResponseEntity<>(rideService.getTotalEarnings(driverId, start, end), HttpStatus.OK);
    }
}
