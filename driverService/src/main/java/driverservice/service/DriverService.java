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
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverMapper driverMapper = DriverMapper.INSTANCE;
    private final DriverWithoutPasswordMapper driverWithoutPasswordMapper = DriverWithoutPasswordMapper.INSTANCE;
    private final DriverWithIdMapper driverWithIdMapper = DriverWithIdMapper.INSTANCE;
    public DriverService(DriverRepository driverRepository)
    {
        this.driverRepository=driverRepository;
    }

    public DriverWithoutPasswordDTO getDriverProfile (Long id)
    {
        Optional<Driver> driverOptional = driverRepository.findById(id);
        if (driverOptional.isEmpty())
        {
            throw new EntityNotFoundException("Driver not found");
        }
        return driverWithoutPasswordMapper.toDTO(driverOptional.get());
    }

    public DriverWithIdDTO createUser(DriverDTO driverDTO) {
        if (driverRepository.findByUsername(driverDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("Driver with the same username already exists");
        }
        if (driverRepository.findByPhone(driverDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("Driver with the same phone already exists");
        }
        Driver driver = driverMapper.toEntity(driverDTO);
        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
        driverRepository.save(driver);
        return driverWithIdMapper.toDTO(driver);
    }
}
