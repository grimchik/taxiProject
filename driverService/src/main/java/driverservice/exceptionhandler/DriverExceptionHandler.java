package driverservice.exceptionhandler;

import driverservice.exception.DriverAuthenticationException;
import driverservice.exception.KafkaSendException;
import driverservice.exception.SamePasswordException;
import driverservice.exception.ServiceUnavailableException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
@Hidden
public class DriverExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(DriverExceptionHandler.class);

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ProblemDetail> handleEntityExistException(EntityExistsException ex)
    {
        log.warn("Entity already exists: {}", ex.getMessage());
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,ex.getMessage());
        problemDetail.setTitle("Exist error");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problemDetail);
    }

    @ExceptionHandler(KafkaSendException.class)
    public ResponseEntity<ProblemDetail> handleKafkaSendException(KafkaSendException ex) {
        log.error("KafkaSendException occurred: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleServiceUnavailableException(ServiceUnavailableException ex)
    {
        log.error("ServiceUnavailableException: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,ex.getMessage());
        problemDetail.setTitle("Service Temporarily Unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail>  handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Invalid values provided. Please check the input data.");
        problemDetail.setTitle("Data Integrity Violation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail>  handleValidationExceptions(MethodArgumentNotValidException ex) {

        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "Field '" + error.getField() + "' - " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + ". " + msg2)
                .orElse("Validation error");
        log.warn("Validation failed: {}", errorMessage);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problemDetail.setTitle("Validation Error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail>  handleEntityNotFoundException(EntityNotFoundException ex) {
        log.info("Entity not found: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Entity Not Found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail>  handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state encountered: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Illegal State");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<ProblemDetail>  handleSamePasswordException(SamePasswordException ex)
    {
        log.info("Attempt to reuse old password: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Same Password Error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException ex) {
        log.warn("Feign client error: {} - {}", ex.getStatusCode(), ex.getReason());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problemDetail.setTitle("Feign Client Error");
        return ResponseEntity.status(ex.getStatusCode()).body(problemDetail);
    }

    @ExceptionHandler(DriverAuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleDriverAuthenticationException(DriverAuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problemDetail.setTitle("Authentication Error");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail>  handleException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}