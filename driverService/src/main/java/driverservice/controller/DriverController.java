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

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfileById(@PathVariable("username") String username)
    {
        return new ResponseEntity<>(driverService.getDriverProfile(username),HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> createDriver( @Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.createDriver(driverDTO),HttpStatus.CREATED);
    }

    @PutMapping("/change-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,@Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.updateProfile(id,driverDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-password/{id}")

    @PatchMapping("/change-username/{id}")

    @PatchMapping("/change-phone/{id}")

    @PatchMapping("/change-name/{id}")

    @DeleteMapping("/{id}")


}
