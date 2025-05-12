package carservice.controller;

import carservice.dto.CarDTO;
import carservice.dto.UpdateCarDTO;
import carservice.service.CarService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/v1/cars/")
public class CarController {
    private static final Logger log = LoggerFactory.getLogger(CarController.class);
    private final CarService carService;
    public CarController(CarService carService)
    {
        this.carService=carService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createCar (@Valid @RequestBody CarDTO carDTO)
    {
        log.info("POST /cars - Creating car: {}", carDTO);
        return new ResponseEntity<>(carService.createCar(carDTO), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAllCars (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        log.info("GET /cars - Retrieving all cars, page={}, size={}", page, size);
        return new ResponseEntity<>(carService.getAllCars(PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCar (@PathVariable("id") Long id)
    {
        log.info("GET /cars/{} - Retrieving car by id", id);
        return new ResponseEntity<>(carService.getCarById(id),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCarById(@PathVariable("id")Long id)
    {
        log.info("DELETE /cars/{} - Deleting car", id);
        carService.deleteCarById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateCarById(@PathVariable("id")Long id,@Valid @RequestBody CarDTO carDTO)
    {
        log.info("PUT /cars/{} - Updating car: {}", id, carDTO);
        return new ResponseEntity<>(carService.updateCarById(id,carDTO),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changeCar(@PathVariable("id")Long id,@Valid @RequestBody UpdateCarDTO updateCarDTO)
    {
        log.info("PATCH /cars/{} - Partially updating car: {}", id, updateCarDTO);
        return new ResponseEntity<>(carService.changeCar(id,updateCarDTO),HttpStatus.OK);
    }
}