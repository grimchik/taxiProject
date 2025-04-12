package driverservice.integrationtest;

import driverservice.entity.Driver;
import driverservice.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class DriverRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Test
    public void testSaveAndFindDriver()
    {
        Driver driver = new Driver();
        driver.setUsername("Oleg");
        driver.setName("Oleg");
        driver.setPhone("+375333565912");
        driver.setPassword("password123");
        driverRepository.save(driver);

        Optional<Driver> savedDriver = driverRepository.findById(driver.getId());
        assertTrue(savedDriver.isPresent());
        assertEquals("Oleg", savedDriver.get().getName());
    }

    @Test
    public void testFindByUsername() {
        Driver driver = new Driver();
        driver.setUsername("Oleg1");
        driver.setName("Oleg");
        driver.setPhone("+375333565913");
        driver.setPassword("password123");
        driverRepository.save(driver);

        Optional<Driver> foundDriver = driverRepository.findByUsername("Oleg1");
        assertTrue(foundDriver.isPresent());
        assertEquals("Oleg", foundDriver.get().getName());
    }

    @Test
    public void testFindByPhone() {
        Driver driver = new Driver();
        driver.setUsername("Oleg2");
        driver.setName("Oleg");
        driver.setPhone("+375333565914");
        driver.setPassword("password123");
        driverRepository.save(driver);

        Optional<Driver> foundDriver = driverRepository.findByPhone("+375333565914");
        assertTrue(foundDriver.isPresent());
        assertEquals("Oleg", foundDriver.get().getName());
    }

    @Test
    public void testFindDriverByCarId() {
        Driver driver = new Driver();
        driver.setUsername("Oleg3");
        driver.setName("Oleg");
        driver.setPhone("+375333565915");
        driver.setPassword("password123");
        driver.setCarId(1L);
        driverRepository.save(driver);

        Optional<Driver> foundDriver = driverRepository.findDriverByCarId(1L);
        assertTrue(foundDriver.isPresent());
        assertEquals("Oleg", foundDriver.get().getName());
    }

    @Test
    public void testSoftDeleteByUsername() {
        Driver driver = new Driver();
        driver.setUsername("Oleg4");
        driver.setName("Oleg");
        driver.setPhone("+375333565919");
        driver.setPassword("password123");
        driverRepository.save(driver);

        driverRepository.softDeleteByUsername("Oleg4");

        Optional<Driver> deletedDriver = driverRepository.findByUsername("Oleg4");
        assertTrue(deletedDriver.isPresent());
        assertTrue(deletedDriver.get().getIsDeleted());
    }

    @Test
    public void testFindAllByIsDeletedFalse() {
        Driver driver1 = new Driver();
        driver1.setUsername("Oleg5");
        driver1.setName("Oleg");
        driver1.setPhone("+375333565917");
        driver1.setPassword("password123");
        driverRepository.save(driver1);

        Driver driver2 = new Driver();
        driver2.setUsername("Petr");
        driver2.setName("Petr");
        driver2.setPhone("+375333565918");
        driver2.setPassword("password123");
        driverRepository.save(driver2);

        driverRepository.softDeleteByUsername("Oleg5");

        List<Driver> activeDrivers = driverRepository.findAllByIsDeletedFalse();
        assertEquals(2, activeDrivers.size());
        assertEquals("Petr", activeDrivers.get(1).getUsername());
    }
}
