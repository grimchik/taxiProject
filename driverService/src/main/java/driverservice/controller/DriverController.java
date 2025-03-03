package driverservice.controller;

import driverservice.dto.*;
import driverservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers/")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService)
    {
        this.driverService=driverService;
    }

    @GetMapping
    public ResponseEntity<?> getProfileById(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                            @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(driverService.getAllDrivers(PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDriverById(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(driverService.getDriverById(id),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createDriver( @Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.createDriver(driverDTO),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,@Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.updateProfile(id,driverDTO),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeDriver(@PathVariable("id") Long id,@Valid @RequestBody UpdateDriverDTO updateDriverDTO)
    {
        return new ResponseEntity<>(driverService.changeDriver(id,updateDriverDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable("id") Long id)
    {
        driverService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}