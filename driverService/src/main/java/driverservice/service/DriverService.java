package driverservice.service;

import driverservice.dto.DriverDTO;
import driverservice.dto.DriverWithIdDTO;
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
        System.out.println(driverWithIdDTO.toString());
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
        if ((driverRepository.findByUsername(driverDTO.getUsername()).isPresent() && !driverDTO.getUsername().equals(driver.getUsername()) || (driverRepository.findByPhone(driverDTO.getPhone()).isPresent() && !driverDTO.getPhone().equals(driver.getPhone()))
        {
            throw new EntityExistsException("Driver with this username or phone already exists");
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
}
