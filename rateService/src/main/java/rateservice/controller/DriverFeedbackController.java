package rateservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rateservice.dto.DriverFeedbackDTO;
import rateservice.dto.UpdateDriverRateDTO;
import rateservice.service.DriverFeedbackService;

@RestController
@RequestMapping("/api/v1/driver-feedbacks/")
public class DriverFeedbackController {
    private final DriverFeedbackService driverFeedbackService;

    public DriverFeedbackController(DriverFeedbackService driverFeedbackService)
    {
        this.driverFeedbackService=driverFeedbackService;
    }

    @PostMapping
    public ResponseEntity<?> createFeedback(@Valid @RequestBody DriverFeedbackDTO driverFeedbackDTO)
    {
        return new ResponseEntity<>(driverFeedbackService.createFeedback(driverFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(driverFeedbackService.getFeedback(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeDriverFeedback(@PathVariable("id") Long id, @Valid @RequestBody UpdateDriverRateDTO updateDriverRateDTO)
    {
        return new ResponseEntity<>(driverFeedbackService.changeDriverFeedback(id,updateDriverRateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id)
    {
        driverFeedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable("id") Long id,@Valid @RequestBody DriverFeedbackDTO driverFeedbackDTO)
    {
        return new ResponseEntity<>(driverFeedbackService.updateFeedback(id,driverFeedbackDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllDriverFeedbacks(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverFeedbackService.getAllDriverFeedbacks(PageRequest.of(page, size)),HttpStatus.OK);
    }
    @GetMapping("/driver-feedbacks/{driverId}")
    public ResponseEntity<?> getAllDriverFeedbacksByUserId(@PathVariable("driverId") Long driverId,
                                                           @RequestParam(value = "page",defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverFeedbackService.getAllDriverFeedbacksById(driverId,PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/driver-rate/{driverId}")
    public ResponseEntity<?> getUserRate(@PathVariable("driverId") Long driverId)
    {
        return new ResponseEntity<>(driverFeedbackService.calculateAverageRating(driverId),HttpStatus.OK);
    }
}
