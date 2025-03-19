package unittest;

import carservice.dto.*;
import carservice.entity.Car;
import carservice.mapper.CarMapper;
import carservice.mapper.CarWithIdMapper;
import carservice.repository.CarRepository;
import carservice.service.CarService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;


import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
    @Mock
    private CarRepository carRepository;

    @Mock
    private CarMapper carMapper;

    @Mock
    private CarWithIdMapper carWithIdMapper;

    @InjectMocks
    private CarService carService;

    private Car car;
    private CarDTO carDTO;
    private CarWithIdDTO carWithIdDTO;
    private UpdateCarDTO updateCarDTO;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setNumber("2222XB-7");

        carDTO = new CarDTO();
        carDTO.setNumber("2222XB-7");

        carWithIdDTO = new CarWithIdDTO();
        carWithIdDTO.setId(1L);
        carWithIdDTO.setNumber("2222XB-7");

        updateCarDTO = new UpdateCarDTO();
        updateCarDTO.setNumber("1222XB-7");
    }

    @Test
    void createCar_shouldSaveCar() {
        when(carRepository.findByNumber(carDTO.getNumber())).thenReturn(Optional.empty());

        doReturn(car).when(carRepository).save(any(Car.class));

        CarWithIdDTO result = carService.createCar(carDTO);

        assertNotNull(result);

        assertEquals(carWithIdDTO.getNumber(), result.getNumber());

        verify(carRepository).save(any(Car.class));
    }


    @Test
    void createCar_shouldThrowExceptionIfCarExists() {
        when(carRepository.findByNumber(carDTO.getNumber())).thenReturn(Optional.of(car));

        assertThrows(EntityExistsException.class, () -> carService.createCar(carDTO));
    }

    @Test
    void getCarById_shouldReturnCar() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarWithIdDTO result = carService.getCarById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCarById_shouldThrowExceptionIfNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.getCarById(1L));
    }

    @Test
    void deleteCarById_shouldDeleteCar() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));
        doNothing().when(carRepository).delete(car);

        carService.deleteCarById(1L);

        verify(carRepository).delete(car);
    }

    @Test
    void deleteCarById_shouldThrowExceptionIfNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.deleteCarById(1L));
    }

    @Test
    void updateCarById_shouldUpdateCar() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarDTO result = carService.updateCarById(1L, carDTO);

        assertNotNull(result);
        assertEquals(carDTO.getNumber(), result.getNumber());
    }

    @Test
    void updateCarById_shouldThrowExceptionIfNotFound() {
        when(carRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> carService.updateCarById(1L, carDTO));
    }

    @Test
    void changeCar_shouldUpdateSpecificFields() {
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        CarWithIdDTO result = carService.changeCar(1L, updateCarDTO);

        assertNotNull(result);
        assertEquals(updateCarDTO.getNumber(), car.getNumber());
    }

    @Test
    void getAllCars_shouldReturnPagedCars() {
        Page<Car> page = new PageImpl<>(List.of(car));

        when(carRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        when(carWithIdMapper.toDTO(car)).thenReturn(carWithIdDTO);

        Page<CarWithIdDTO> mappedPage = page.map(carWithIdMapper::toDTO);

        Page<CarWithIdDTO> result = carService.getAllCars(PageRequest.of(0, 10));
        assertEquals(mappedPage.getTotalElements(), result.getTotalElements());
        assertEquals(mappedPage.getContent(), result.getContent());
    }

}