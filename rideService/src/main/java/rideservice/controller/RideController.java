package rideservice.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rideservice.dto.RideDTO;
import rideservice.dto.UpdateRideDTO;
import rideservice.service.RideService;

@RestController
@RequestMapping("/api/v1/rides/")
public class RideController {
    private final RideService rideService;
    public RideController (RideService rideService)
    {
        this.rideService=rideService;
    }
    @PostMapping
    public ResponseEntity<?> createRide(@Valid @RequestBody RideDTO rideDTO)
    {
        return new ResponseEntity<>(rideService.createRide(rideDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeRide(@PathVariable("id") Long id, @Valid @RequestBody UpdateRideDTO updateRideDTO)
    {
        return new ResponseEntity<>(rideService.changeRide(id,updateRideDTO),HttpStatus.OK);
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

    @GetMapping
    public ResponseEntity<?> getAllRides(@RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(rideService.getAllRides(PageRequest.of(page, size)),HttpStatus.OK);
    }
}
