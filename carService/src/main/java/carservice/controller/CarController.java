package carservice.controller;

import carservice.dto.CarDTO;
import carservice.dto.UpdateCarDTO;
import carservice.service.CarService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cars/")
public class CarController {
    private final CarService carService;
    public CarController(CarService carService)
    {
        this.carService=carService;
    }

    @PostMapping
    public ResponseEntity<?> createCar (@Valid @RequestBody CarDTO carDTO)
    {
        return new ResponseEntity<>(carService.createCar(carDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getAllCars (@RequestParam(value = "page",defaultValue = "0") Integer page,
                                         @RequestParam(value = "size",defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(carService.getAllCars(PageRequest.of(page, size)),HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCar (@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(carService.getCarById(id),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarById(@PathVariable("id")Long id)
    {
        carService.deleteCarById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarById(@PathVariable("id")Long id,@Valid @RequestBody CarDTO carDTO)
    {
        return new ResponseEntity<>(carService.updateCarById(id,carDTO),HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changeCar(@PathVariable("id")Long id,@Valid @RequestBody UpdateCarDTO updateCarDTO)
    {
        return new ResponseEntity<>(carService.changeCar(id,updateCarDTO),HttpStatus.OK);
    }
}