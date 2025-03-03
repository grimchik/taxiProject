package driverservice.service;

import driverservice.dto.*;
import driverservice.entity.Driver;
import driverservice.exception.SamePasswordException;
import driverservice.mapper.DriverMapper;
import driverservice.mapper.DriverWithIdMapper;
import driverservice.mapper.DriverWithoutPasswordMapper;
import driverservice.repository.DriverRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final PasswordEncoder passwordEncoder;
    private final DriverMapper driverMapper = DriverMapper.INSTANCE;
    private final DriverWithoutPasswordMapper driverWithoutPasswordMapper = DriverWithoutPasswordMapper.INSTANCE;
    private final DriverWithIdMapper driverWithIdMapper = DriverWithIdMapper.INSTANCE;

    public DriverService(DriverRepository driverRepository, PasswordEncoder passwordEncoder) {
        this.driverRepository = driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public DriverWithIdDTO createDriver(DriverDTO driverDTO) {
        if (driverRepository.findByUsername(driverDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
        if (driverRepository.findByPhone(driverDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
        Driver driver = driverMapper.toEntity(driverDTO);
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

    @Transactional
    public void deleteById(Long id) {
        Driver driver = findActiveDriverById(id);
        driverRepository.softDeleteByUsername(driver.getUsername());
    }

    private Driver findActiveDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        checkIsDeleted(driver.getIsDeleted());
        return driver;
    }

    private void checkIsDeleted(boolean deleted) {
        if (deleted) {
            throw new IllegalStateException("Driver has already been deleted");
        }
    }

    public DriverWithIdDTO getDriverById(Long id) {
        Driver driver = findActiveDriverById(id);
        return driverWithIdMapper.toDTO(driver);
    }

    public Page<DriverWithIdDTO> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverWithIdMapper::toDTO);
    }

    @Transactional
    public DriverWithoutPasswordDTO updateProfile(Long id, DriverDTO driverDTO) {
        Driver driver = findActiveDriverById(id);
        checkUsername(driver, driverDTO.getUsername());
        checkPhone(driver, driverDTO.getPhone());
        checkPassword(driver, driverDTO.getPassword());
        driver.setName(driverDTO.getName());
        driver.setUsername(driverDTO.getUsername());
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driver.setPhone(driverDTO.getPhone());
        driverRepository.save(driver);
        return driverWithoutPasswordMapper.toDTO(driver);
    }

    @Transactional
    public DriverWithIdDTO changeDriver(Long id, UpdateDriverDTO updateDriverDTO) {
        Driver driver = findActiveDriverById(id);

        if (updateDriverDTO.getUsername() != null && !updateDriverDTO.getUsername().isBlank()) {
            checkUsername(driver, updateDriverDTO.getUsername());
            driver.setUsername(updateDriverDTO.getUsername());
        }

        if (updateDriverDTO.getPhone() != null && !updateDriverDTO.getPhone().isBlank()) {
            checkPhone(driver, updateDriverDTO.getPhone());
            driver.setPhone(updateDriverDTO.getPhone());
        }

        if (updateDriverDTO.getName() != null && !updateDriverDTO.getName().isBlank()) {
            driver.setName(updateDriverDTO.getName());
        }

        if (updateDriverDTO.getPassword() != null && !updateDriverDTO.getPassword().isBlank()) {
            checkPassword(driver, updateDriverDTO.getPassword());
            driver.setPassword(passwordEncoder.encode(updateDriverDTO.getPassword()));
        }

        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

    private void checkUsername(Driver driver, String username) {
        if (driverRepository.findByUsername(username).isPresent() &&
                !username.equals(driver.getUsername())) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
    }

    private void checkPhone(Driver driver, String phone) {
        if (driverRepository.findByPhone(phone).isPresent() &&
                !phone.equals(driver.getPhone())) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
    }

    private void checkPassword(Driver driver, String password) {
        if (passwordEncoder.matches(password, driver.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }
    }
}
