package driverservice.client;

import driverservice.configuration.FeignConfiguration;
import driverservice.dto.CarWithIdDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "car-service", url = "http://localhost:8080/api/v1/cars", configuration = FeignConfiguration.class)
public interface CarServiceClient {
    @GetMapping("/{id}")
    CarWithIdDTO getCarById(@PathVariable("id") Long id);
}
