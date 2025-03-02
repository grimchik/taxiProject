package driverservice.service;

import driverservice.dto.*;
import driverservice.dto.DriverWithoutPasswordDTO;
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
    private final DriverMapper driverMapper = DriverMapper.INSTANCE;
    private final DriverWithoutPasswordMapper driverWithoutPasswordMapper = DriverWithoutPasswordMapper.INSTANCE;
    private final DriverWithIdMapper driverWithIdMapper = DriverWithIdMapper.INSTANCE;
    private final PasswordEncoder passwordEncoder;

    public DriverService(DriverRepository driverRepository, PasswordEncoder passwordEncoder)
    {
        this.driverRepository=driverRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public DriverWithIdDTO createDriver(DriverDTO driverDTO) throws EntityExistsException{
        if (driverRepository.findByUsername(driverDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
        if (driverRepository.findByPhone(driverDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
        Driver driver = driverMapper.toEntity(driverDTO);
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        driverRepository.save(driver);
        return  driverWithIdMapper.toDTO(driver);
    }

    @Transactional
    public DriverWithoutPasswordDTO updateProfile(Long id ,DriverDTO driverDTO)
    {
        Driver driver = findActiveDriverById(id);
        if (driverRepository.findByUsername(driverDTO.getUsername())
                .filter(d -> !d.getId().equals(driver.getId())).isPresent()) {
            throw new EntityExistsException("Driver with this username already exists");
        }

        if (driverRepository.findByPhone(driverDTO.getPhone())
                .filter(d -> !d.getId().equals(driver.getId())).isPresent()) {
            throw new EntityExistsException("Driver with this phone already exists");
        }

        if (passwordEncoder.matches(driverDTO.getPassword(), driver.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }
        driver.setName(driverDTO.getName());
        driver.setUsername(driverDTO.getUsername());
        driver.setPassword(passwordEncoder.encode(driverDTO.getPassword()));
        driver.setPhone(driverDTO.getPhone());
        driverRepository.save(driver);
        return driverWithoutPasswordMapper.toDTO(driver);
    }

    private Driver findActiveDriverById(Long id) {
        Optional<Driver> driverOptional = driverRepository.findById(id);
        if (driverOptional.isEmpty()) {
            throw new EntityNotFoundException("Driver not found");
        }
        Driver driver = driverOptional.get();
        if (driver.getIsDeleted()) {
            throw new IllegalStateException("Driver has already been deleted");
        }
        return driver;
    }


    @Transactional
    public void deleteById(Long id)
    {
        Driver driver = findActiveDriverById(id);
        driverRepository.softDeleteByUsername(driver.getUsername());
    }

    public Page<DriverWithIdDTO> getAllDrivers(Pageable pageable) {
        return driverRepository.findAll(pageable).map(driverWithIdMapper::toDTO);
    }

    public DriverWithIdDTO getDriverById (Long id)
    {
        Driver driver = findActiveDriverById(id);
        return driverWithIdMapper.toDTO(driver);
    }

    @Transactional
    public DriverWithIdDTO changeDriver (Long id, UpdateDriverDTO updateDriverDTO)
    {
        Driver driver = findActiveDriverById(id);
        if(updateDriverDTO.getUsername() != null)
        {
            if (driverRepository.findByUsername(updateDriverDTO.getUsername()).isPresent() && !updateDriverDTO.getUsername().equals(driver.getUsername()))
            {
                throw new EntityExistsException("Driver with the same username already exists");
            }
            driver.setUsername(updateDriverDTO.getUsername());
        }

        if(updateDriverDTO.getPhone() != null)
        {
            if (driverRepository.findByPhone(updateDriverDTO.getPhone()).isPresent() && !updateDriverDTO.getPhone().equals(driver.getPhone()))
            {
                throw new EntityExistsException("Driver with the same phone already exists");
            }
            driver.setPhone(updateDriverDTO.getPhone());
        }

        if (updateDriverDTO.getName() != null)
        {
            driver.setName(updateDriverDTO.getName());
        }

        if (updateDriverDTO.getPassword() !=null)
        {
            if (passwordEncoder.matches(updateDriverDTO.getPassword(), driver.getPassword())) {
                throw new SamePasswordException("The new password cannot be the same as the old password.");
            }
            driver.setPassword(updateDriverDTO.getPassword());
        }

        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }

}