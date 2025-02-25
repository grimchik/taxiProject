package carservice.service;

import carservice.dto.*;
import carservice.entity.Car;
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

    @Transactional
    public void deleteCarById(Long id)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty())
        {
            throw new EntityNotFoundException("Car not found");
        }
        Car car = carOptional.get();
        carRepository.delete(car);
    }

    @Transactional
    public CarDTO updateCarById(Long id,CarDTO carDTO)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty())
        {
            throw new EntityNotFoundException("Car not found");
        }
        if (!carOptional.get().getNumber().equals(carDTO.getNumber()) &&
                carRepository.findByNumber(carDTO.getNumber()).isPresent()) {
            throw new EntityExistsException("Car with " + carDTO.getNumber() + " already exists");
        }
        Car car = carOptional.get();
        car.setDescription(carDTO.getDescription());
        car.setModel(carDTO.getModel());
        car.setBrand(carDTO.getBrand());
        car.setNumber(carDTO.getNumber());
        car.setCategory(carDTO.getCategory());
        carRepository.save(car);
        return carMapper.toDTO(car);
    }

    @Transactional
    public String changeNumber (Long id, NumberDTO numberDTO)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty())
        {
            throw new EntityNotFoundException("Car not found");
        }
        if (carRepository.findByNumber(numberDTO.getNumber()).isPresent())
        {
            throw new EntityExistsException("Car with "+ numberDTO.getNumber() +" already exists");
        }
        Car car = carOptional.get();
        car.setNumber(numberDTO.getNumber());
        carRepository.save(car);
        return "Number changed successfully";
    }

    @Transactional
    public String changeCategory(Long id, CategoryDTO categoryDTO)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty())
        {
            throw new EntityNotFoundException("Car not found");
        }
        Car car = carOptional.get();
        car.setCategory(categoryDTO.getCategory());
        carRepository.save(car);
        return "Category changed successfully";
    }

    @Transactional
    public String changeInformationAboutCar(Long id, CarInfoDTO carInfoDTO)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        if (carOptional.isEmpty())
        {
            throw new EntityNotFoundException("Car not found");
        }
        Car car = carOptional.get();
        car.setModel(carInfoDTO.getModel());
        car.setBrand(carInfoDTO.getBrand());
        car.setColor(carInfoDTO.getColor());
        car.setDescription(carInfoDTO.getDescription());
        carRepository.save(car);
        return "Car information changed successfully";
    }
}