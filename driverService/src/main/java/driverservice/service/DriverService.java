package driverservice.service;

import driverservice.dto.DriverDTO;
import driverservice.dto.DriverWithIdDTO;
import driverservice.dto.DriverWithoutPasswordDTO;
import driverservice.entity.Driver;
import driverservice.mapper.DriverMapper;
import driverservice.mapper.DriverWithIdMapper;
import driverservice.mapper.DriverWithoutPasswordMapper;
import driverservice.repository.DriverRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        System.out.println(driverWithIdDTO.toString());
        return driverWithIdDTO;
    }
}
