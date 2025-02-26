package rideservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rideservice.dto.RideDTO;
import rideservice.dto.StatusDTO;
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

    @PatchMapping("/change-status/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable("id") Long id, @Valid @RequestBody StatusDTO statusDTO)
    {
        return new ResponseEntity<>(rideService.changeStatusById(id,statusDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-locations/{id}")
    public ResponseEntity<?> changeLocationsById(@PathVariable("id") Long id, @Valid @RequestBody RideDTO rideDTO)
    {
        return new ResponseEntity<>(rideService.changeLocations(id,rideDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRide(@PathVariable("id") Long id)
    {
        rideService.deleteRideById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRideById(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(rideService.getRideById(id),HttpStatus.OK);
    }
}
