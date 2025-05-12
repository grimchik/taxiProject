package rateservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rateservice.dto.ClientFeedbackDTO;
import rateservice.dto.UpdateClientRateDTO;
import rateservice.service.ClientFeedbackService;

@RestController
@RequestMapping("/api/v1/client-feedbacks/")
public class ClientFeedbackController {
    private final ClientFeedbackService clientFeedbackService;
    private static final Logger log = LoggerFactory.getLogger(ClientFeedbackController.class);

    public ClientFeedbackController(ClientFeedbackService clientFeedbackService) {
        this.clientFeedbackService = clientFeedbackService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createFeedback(@Valid @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        log.info("POST /client-feedbacks - Creating new feedback for user ID: {}, ride ID: {}",
                clientFeedbackDTO.getUserId(), clientFeedbackDTO.getRideId());
        return new ResponseEntity<>(clientFeedbackService.createFeedback(clientFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFeedback(@PathVariable("id") Long id) {
        log.info("GET /client-feedbacks/{} - Retrieving feedback by ID", id);
        return new ResponseEntity<>(clientFeedbackService.getFeedback(id), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeClientFeedback(@PathVariable("id") Long id,
                                                  @Valid @RequestBody UpdateClientRateDTO updateClientRateDTO) {
        log.info("PATCH /client-feedbacks/{} - Partially updating feedback", id);
        return new ResponseEntity<>(clientFeedbackService.changeClientFeedback(id, updateClientRateDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteFeedback(@PathVariable("id") Long id) {
        log.info("DELETE /client-feedbacks/{} - Deleting feedback", id);
        clientFeedbackService.deleteFeedback(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateFeedback(@PathVariable("id") Long id,
                                            @Valid @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        log.info("PUT /client-feedbacks/{} - Fully updating feedback", id);
        return new ResponseEntity<>(clientFeedbackService.updateFeedback(id, clientFeedbackDTO), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllClientFeedbacks(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /client-feedbacks - Retrieving all feedbacks, page={}, size={}", page, size);
        return new ResponseEntity<>(clientFeedbackService.getAllClientFeedbacks(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/user-feedbacks/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllClientFeedbacksByUserId(@PathVariable("userId") Long userId,
                                                           @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /client-feedbacks/user-feedbacks/{} - Retrieving all feedbacks by user ID, page={}, size={}",
                userId, page, size);
        return new ResponseEntity<>(clientFeedbackService.getAllClientFeedbacksById(userId, PageRequest.of(page, size)), HttpStatus.OK);
    }

    @GetMapping("/user-rate/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserRate(@PathVariable("userId") Long userId) {
        log.info("GET /client-feedbacks/user-rate/{} - Calculating average rating for user ID", userId);
        return new ResponseEntity<>(clientFeedbackService.calculateAverageRating(userId), HttpStatus.OK);
    }
}
