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

    public DriverWithIdDTO getDriverProfile (String username) throws EntityNotFoundException
    {
        Optional<Driver> driverOptional = driverRepository.findByUsername(username);
        if (driverOptional.isEmpty())
        {
            throw new EntityNotFoundException("Driver not found");
        }
        return driverWithIdMapper.toDTO(driverOptional.get());
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
        DriverWithIdDTO driverWithIdDTO=driverWithIdMapper.toDTO(driver);
        return driverWithIdDTO;
    }

    @Transactional
    public DriverWithoutPasswordDTO updateProfile(Long id ,DriverDTO driverDTO)
    {
        Optional<Driver> driverOptional = driverRepository.findById(id);
        if (driverOptional.isEmpty())
        {
            throw new EntityNotFoundException("Driver not found");
        }
        Driver driver =driverOptional.get();
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
    public String changePasswordById(Long id, PasswordDTO password) {
        Driver driver = findActiveDriverById(id);
        if (passwordEncoder.matches(password.getPassword(), driver.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }

        driver.setPassword(passwordEncoder.encode(password.getPassword()));
        driverRepository.save(driver);
        return "Password changed successfully";
    }

    @Transactional
    public String changePhoneById(Long id, PhoneDTO phone) {
        Driver driver = findActiveDriverById(id);

        if (driverRepository.findByPhone(phone.getPhone()).isPresent()) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }

        driver.setPhone(phone.getPhone());
        driverRepository.save(driver);
        return "Phone changed successfully";
    }

    @Transactional
    public String changeUsernameById(Long id, UsernameDTO username)
    {
        Driver driver = findActiveDriverById(id);
        if (driverRepository.findByUsername(username.getUsername()).isPresent())
        {
            throw new EntityExistsException("Driver with the same username already exists");
        }
        driver.setUsername(username.getUsername());
        driverRepository.save(driver);
        return "Username changed successfully";
    }

    @Transactional
    public String changeNameById(Long id, NameDTO name)
    {
        Driver driver = findActiveDriverById(id);
        driver.setUsername(name.getName());
        driverRepository.save(driver);
        return "Name changed successfully";
    }

    @Transactional
    public void deleteById(Long id)
    {
        Optional<Driver> driverOptional =driverRepository.findById(id);
        if (driverOptional.isEmpty()) {
            throw new EntityNotFoundException("Driver not found");
        }
        Driver driver = driverOptional.get();
        if (driver.getIsDeleted()) {
            throw new IllegalStateException("Driver with " + driver.getUsername() + " has already been deleted");
        }
        driverRepository.softDeleteByUsername(driver.getUsername());
    }
}
