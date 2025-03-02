package rateservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rateservice.dto.ClientFeedbackDTO;
import rateservice.dto.RateDTO;
import rateservice.dto.UpdateClientRateDTO;
import rateservice.service.ClientFeedbackService;

@RestController
@RequestMapping("/api/v1/client-feedbacks/")
public class ClientFeedbackController {
    private final ClientFeedbackService clientFeedbackService;

    public ClientFeedbackController(ClientFeedbackService clientFeedbackService) {
        this.clientFeedbackService = clientFeedbackService;
    }

    @PostMapping
    public ResponseEntity<?> createFeedback(@Valid @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        return new ResponseEntity<>(clientFeedbackService.createFeedback(clientFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedback(@PathVariable("id") Long id) {
        return new ResponseEntity<>(clientFeedbackService.getFeedback(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeClientFeedback(@PathVariable("id") Long id, @Valid @RequestBody UpdateClientRateDTO updateClientRateDTO) {
        return new ResponseEntity<>(clientFeedbackService.changeClientFeedback(id, updateClientRateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id) {
        clientFeedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable("id") Long id, @Valid @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        return new ResponseEntity<>(clientFeedbackService.updateFeedback(id, clientFeedbackDTO), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllClientFeedbacks(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(clientFeedbackService.getAllClientFeedbacks(PageRequest.of(page, size)),HttpStatus.OK);
    }
}
