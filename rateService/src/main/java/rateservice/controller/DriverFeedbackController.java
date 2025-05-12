package rateservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rateservice.dto.DriverFeedbackDTO;
import rateservice.dto.UpdateDriverRateDTO;
import rateservice.service.DriverFeedbackService;

@RestController
@RequestMapping("/api/v1/driver-feedbacks/")
public class DriverFeedbackController {

    private final DriverFeedbackService driverFeedbackService;
    private static final Logger log = LoggerFactory.getLogger(DriverFeedbackController.class);

    public DriverFeedbackController(DriverFeedbackService driverFeedbackService) {
        this.driverFeedbackService = driverFeedbackService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createFeedback(@Valid @RequestBody DriverFeedbackDTO driverFeedbackDTO) {
        log.info("POST /driver-feedbacks - Creating new feedback from driver ID: {}, to ride ID: {}",
                driverFeedbackDTO.getDriverId(), driverFeedbackDTO.getRideId());
        return new ResponseEntity<>(driverFeedbackService.createFeedback(driverFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFeedback(@PathVariable("id") Long id) {
        log.info("GET /driver-feedbacks/{} - Retrieving feedback by ID", id);
        return new ResponseEntity<>(driverFeedbackService.getFeedback(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeDriverFeedback(@PathVariable("id") Long id,
                                                  @Valid @RequestBody UpdateDriverRateDTO updateDriverRateDTO) {
        log.info("PATCH /driver-feedbacks/{} - Partially updating feedback", id);
        return new ResponseEntity<>(driverFeedbackService.changeDriverFeedback(id, updateDriverRateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id) {
        log.info("DELETE /driver-feedbacks/{} - Deleting feedback", id);
        driverFeedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateFeedback(@PathVariable("id") Long id,
                                            @Valid @RequestBody DriverFeedbackDTO driverFeedbackDTO) {
        log.info("PUT /driver-feedbacks/{} - Fully updating feedback", id);
        return new ResponseEntity<>(driverFeedbackService.updateFeedback(id, driverFeedbackDTO), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllDriverFeedbacks(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /driver-feedbacks - Retrieving all driver feedbacks, page={}, size={}", page, size);
        return new ResponseEntity<>(driverFeedbackService.getAllDriverFeedbacks(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/driver-feedbacks/{driverId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllDriverFeedbacksByUserId(@PathVariable("driverId") Long driverId,
                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /driver-feedbacks/driver-feedbacks/{} - Retrieving all feedbacks for driver ID, page={}, size={}", driverId, page, size);
        return new ResponseEntity<>(driverFeedbackService.getAllDriverFeedbacksById(driverId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/driver-rate/{driverId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserRate(@PathVariable("driverId") Long driverId) {
        log.info("GET /driver-feedbacks/driver-rate/{} - Calculating average rating for driver ID", driverId);
        return new ResponseEntity<>(driverFeedbackService.calculateAverageRating(driverId), HttpStatus.OK);
    }
}
