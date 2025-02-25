package rideservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rideservice.dto.RideDTO;
import rideservice.service.RideService;

@RestController
@RequestMapping("/api/ride/")
public class RideController {
    private final RideService rideService;
    public RideController (RideService rideService)
    {
        this.rideService=rideService;
    }
    @PostMapping("/create-ride")
    public ResponseEntity<?> createRide(@Valid @RequestBody RideDTO rideDTO)
    {
        return new ResponseEntity<>(rideService.createRide(rideDTO), HttpStatus.CREATED);
    }
}
