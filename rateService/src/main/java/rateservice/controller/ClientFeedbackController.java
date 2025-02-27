package rateservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rateservice.dto.ClientFeedbackDTO;
import rateservice.dto.RateDTO;
import rateservice.service.ClientFeedbackService;

@RestController
@RequestMapping("/api/client-feedback/")
public class ClientFeedbackController {
    private final ClientFeedbackService clientFeedbackService;

    public ClientFeedbackController(ClientFeedbackService clientFeedbackService) {
        this.clientFeedbackService = clientFeedbackService;
    }

    @PostMapping("/create-feedback")
    public ResponseEntity<?> createFeedback(@Valid @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        return new ResponseEntity<>(clientFeedbackService.createFeedback(clientFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable("id") Long id) {
        return new ResponseEntity<>(clientFeedbackService.getFeedback(id), HttpStatus.OK);
    }

    @PatchMapping("/change-rate/{id}")
    public ResponseEntity<?> changeRate(@PathVariable("id") Long id, @Valid @RequestBody RateDTO rateDTO) {
        return new ResponseEntity<>(clientFeedbackService.changeRate(id, rateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id) {
        clientFeedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update-feedback/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable("id") Long id, @Valid @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        return new ResponseEntity<>(clientFeedbackService.updateFeedback(id, clientFeedbackDTO), HttpStatus.OK);
    }
}
