package carservice.controller;

import carservice.dto.CarDTO;
import carservice.dto.NumberDTO;
import carservice.service.CarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/car/")
public class CarController {
    private final CarService carService;
    public CarController(CarService carService)
    {
        this.carService=carService;
    }

    @PostMapping("/registrate-car")
    public ResponseEntity<?> createCar (@Valid @RequestBody CarDTO carDTO)
    {
        return new ResponseEntity<>(carService.createCar(carDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{number}")
    public ResponseEntity<?> getCar (@PathVariable("number") String number)
    {
        return new ResponseEntity<>(carService.getCarByNumber(number),HttpStatus.OK);
    }
}