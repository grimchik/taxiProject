package carservice.service;

import carservice.dto.CarDTO;
import carservice.dto.NumberDTO;
import carservice.entity.Car;
import carservice.dto.CarWithIdDTO;
import carservice.mapper.CarMapper;
import carservice.mapper.CarWithIdMapper;
import carservice.repository.CarRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CarService {
    private final CarRepository carRepository;
    private final CarMapper carMapper= CarMapper.INSTANCE;
    private final CarWithIdMapper carWithIdMapper= CarWithIdMapper.INSTANCE;
    public CarService(CarRepository carRepository)
    {
        this.carRepository=carRepository;
    }

    @Transactional
    public CarWithIdDTO createCar(CarDTO carDTO)
    {
        Optional<Car> carOptional=carRepository.findByNumber(carDTO.getNumber());
        if (carOptional.isPresent())
        {
            throw new EntityExistsException("Car with "+ carDTO.getNumber() +" already exists");
        }
        Car car = carMapper.toEntity(carDTO);
        carRepository.save(car);
        return carWithIdMapper.toDTO(car);
    }

    public CarWithIdDTO getCarByNumber(String number)
    {
        Optional<Car> carOptional = carRepository.findByNumber(number);
        if(carOptional.isEmpty())
        {
            throw new EntityNotFoundException("Car not found");
        }
        return carWithIdMapper.toDTO(carOptional.get());
    }
}
