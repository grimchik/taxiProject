package clientservice.client;

import clientservice.configuration.FeignConfiguration;
import clientservice.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "car-service", url = "http://localhost:8080/api/v1/cars", configuration = FeignConfiguration.class)
public interface CarServiceClient {
    @GetMapping("/")
    Page<CarWithIdDTO> getAllCars (Pageable pageable);

    @PostMapping("/")
    CarWithIdDTO createCar(@RequestBody CarCreateDTO request);

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    CarWithIdDTO changeCar(@PathVariable("id") Long carId,
                           @RequestBody UpdateCarDTO updateCarDTO
    );

    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable("id") Long carId);
}
