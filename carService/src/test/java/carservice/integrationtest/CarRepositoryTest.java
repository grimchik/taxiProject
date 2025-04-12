package carservice.integrationtest;

import carservice.entity.Car;
import carservice.enums.Category;
import carservice.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    public void setup() {
        carRepository.deleteAll();
    }

    @Test
    public void testSaveCar() {
        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setDescription("Reliable compact sedan");
        car.setColor("White");
        car.setCategory(Category.ECONOMY.name());
        car.setNumber("1234AB-3");

        Car savedCar = carRepository.save(car);
        assertThat(savedCar.getId()).isNotNull();
    }

    @Test
    public void testFindByNumber() {
        Car car = new Car();
        car.setBrand("Honda");
        car.setModel("Civic");
        car.setDescription("Compact car");
        car.setColor("Black");
        car.setCategory(Category.COMFORT.name());
        car.setNumber("5678CD-5");

        carRepository.save(car);

        Optional<Car> found = carRepository.findByNumber("5678CD-5");
        assertThat(found).isPresent();
        Car foundCar = found.get();
        assertThat(foundCar.getBrand()).isEqualTo("Honda");
        assertThat(foundCar.getCategory()).isEqualTo(Category.COMFORT.name());
    }

    @Test
    public void testFindAllByCategory() {
        Car car1 = new Car();
        car1.setBrand("BMW");
        car1.setModel("3 Series");
        car1.setDescription("Business sedan");
        car1.setColor("Blue");
        car1.setCategory(Category.BUSINESS.name());
        car1.setNumber("9012EF-4");

        Car car2 = new Car();
        car2.setBrand("Mercedes");
        car2.setModel("C Class");
        car2.setDescription("Luxury sedan");
        car2.setColor("Silver");
        car2.setCategory(Category.BUSINESS.name());
        car2.setNumber("3456GH-2");

        Car car3 = new Car();
        car3.setBrand("Ford");
        car3.setModel("Focus");
        car3.setDescription("Economy car");
        car3.setColor("Red");
        car3.setCategory(Category.ECONOMY.name());
        car3.setNumber("7890IJ-6");

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);

        List<Car> businessCars = carRepository.findAllByCategory(Category.BUSINESS.name());
        assertThat(businessCars).hasSize(2);

        List<Car> economyCars = carRepository.findAllByCategory(Category.ECONOMY.name());
        assertThat(economyCars).hasSize(1);
    }
}