package driverservice.repository;

import driverservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver,Long> {
    Optional<Driver> findByUsername(String username);
    Optional<Driver> findByPhone(String phone);
    List<Driver> findAllByIsDeletedFalse();
    @Modifying
    @Query("UPDATE Driver d SET d.isDeleted = true WHERE d.username = :username")
    void softDeleteByUsername(@Param("username") String username);
}