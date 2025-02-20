package driverservice.controller;

import driverservice.dto.DriverDTO;
import driverservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver/")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService)
    {
        this.driverService=driverService;
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(driverService.getDriverProfile(id),HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> getProfileById( @Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.createDriver(driverDTO),HttpStatus.CREATED);
    }
}
