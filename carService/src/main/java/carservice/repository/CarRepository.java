package carservice.repository;

import carservice.entity.Car;
import carservice.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car,Long> {
    List<Car> findAllByCategory(Category category);
    Optional<Car> findByNumber(String number);
}