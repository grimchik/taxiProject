package carservice.service;

import carservice.dto.*;
import carservice.entity.Car;
import carservice.mapper.CarMapper;
import carservice.mapper.CarWithIdMapper;
import carservice.repository.CarRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        if (carOptional.isPresent()) {
            throw new EntityExistsException("Car with " + carDTO.getNumber() + " already exists");
        }
        Car car = carMapper.toEntity(carDTO);
        carRepository.save(car);
        return carWithIdMapper.toDTO(car);
    }

    public CarWithIdDTO getCarById(Long id)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        carOptional.orElseThrow(() -> new EntityNotFoundException("Car not found"));
        return carWithIdMapper.toDTO(carOptional.get());
    }

    @Transactional
    public void deleteCarById(Long id)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        carOptional.orElseThrow(() -> new EntityNotFoundException("Car not found"));
        Car car = carOptional.get();
        carRepository.delete(car);
    }

    private void checkIfCarNumberExists(Optional<Car> carOptional, String number) {
        if (carOptional.isPresent() &&
                !carOptional.get().getNumber().equals(number) &&
                carRepository.findByNumber(number).isPresent()) {
            throw new EntityExistsException("Car with " + number + " already exists");
        }
    }

    @Transactional
    public CarDTO updateCarById(Long id,CarDTO carDTO)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        carOptional.orElseThrow(() -> new EntityNotFoundException("Car not found"));
        checkIfCarNumberExists(carOptional, carDTO.getNumber());
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
    public CarWithIdDTO changeCar (Long id,UpdateCarDTO updateCarDTO)
    {
        Optional<Car> carOptional = carRepository.findById(id);
        carOptional.orElseThrow(() -> new EntityNotFoundException("Car not found"));
        Car car = carOptional.get();
        if(updateCarDTO.getNumber() != null)
        {
            checkIfCarNumberExists(carOptional, updateCarDTO.getNumber());
            car.setNumber(updateCarDTO.getNumber());
        }

        if (updateCarDTO.getColor() != null && !updateCarDTO.getColor().isBlank()) {
            car.setColor(updateCarDTO.getColor());
        }

        if (updateCarDTO.getDescription() != null && !updateCarDTO.getDescription().isBlank()) {
            car.setDescription(updateCarDTO.getDescription());
        }

        if (updateCarDTO.getCategory() != null) {
            car.setCategory(updateCarDTO.getCategory());
        }

        if (updateCarDTO.getModel() != null && !updateCarDTO.getModel().isBlank()) {
            car.setModel(updateCarDTO.getModel());
        }

        if (updateCarDTO.getBrand() != null && !updateCarDTO.getBrand().isBlank()) {
            car.setBrand(updateCarDTO.getBrand());
        }

        carRepository.save(car);
        return carWithIdMapper.toDTO(car);
    }

    public Page<CarWithIdDTO> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable).map(carWithIdMapper::toDTO);
    }
}