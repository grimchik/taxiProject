package carservice.controller;

import carservice.dto.CarDTO;
import carservice.dto.CarInfoDTO;
import carservice.dto.CategoryDTO;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarById(@PathVariable("id")Long id)
    {
        carService.deleteCarById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update-car/{id}")
    public ResponseEntity<?> updateCarById(@PathVariable("id")Long id,@Valid @RequestBody CarDTO carDTO)
    {
        return new ResponseEntity<>(carService.updateCarById(id,carDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-number/{id}")
    public ResponseEntity<?> changeNumberById(@PathVariable("id")Long id,@Valid @RequestBody NumberDTO numberDTO)
    {
        return new ResponseEntity<>(carService.changeNumber(id,numberDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-category/{id}")
    public ResponseEntity<?> changeCategoryById(@PathVariable("id")Long id,@Valid @RequestBody CategoryDTO categoryDTO)
    {
        return new ResponseEntity<>(carService.changeCategory(id,categoryDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-car-info/{id}")
    public ResponseEntity<?> changeCarInfoById(@PathVariable("id")Long id,@Valid @RequestBody CarInfoDTO carinfoDTO)
    {
        return new ResponseEntity<>(carService.changeInformationAboutCar(id,carinfoDTO),HttpStatus.OK);
    }
}