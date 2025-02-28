package rateservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rateservice.dto.DriverFeedbackDTO;
import rateservice.dto.RateDTO;
import rateservice.service.DriverFeedbackService;

@RestController
@RequestMapping("/api/driver-feedback/")
public class DriverFeedbackController {
    private final DriverFeedbackService driverFeedbackService;

    public DriverFeedbackController(DriverFeedbackService driverFeedbackService)
    {
        this.driverFeedbackService=driverFeedbackService;
    }

    @PostMapping("/create-feedback")
    public ResponseEntity<?> createFeedback(@Valid @RequestBody DriverFeedbackDTO driverFeedbackDTO)
    {
        return new ResponseEntity<>(driverFeedbackService.createFeedback(driverFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(driverFeedbackService.getFeedback(id), HttpStatus.OK);
    }

    @PatchMapping("/change-rate/{id}")
    public ResponseEntity<?> changeRate(@PathVariable("id") Long id,@Valid @RequestBody RateDTO rateDTO)
    {
        return new ResponseEntity<>(driverFeedbackService.changeRate(id,rateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id)
    {
        driverFeedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update-feedback/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable("id") Long id,@Valid @RequestBody DriverFeedbackDTO driverFeedbackDTO)
    {
        return new ResponseEntity<>(driverFeedbackService.updateFeedback(id,driverFeedbackDTO), HttpStatus.OK);
    }
}
