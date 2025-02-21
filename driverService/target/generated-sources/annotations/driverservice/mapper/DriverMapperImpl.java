package driverservice.mapper;

import driverservice.dto.DriverDTO;
import driverservice.entity.Driver;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-21T17:59:54+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class DriverMapperImpl implements DriverMapper {

    @Override
    public Driver toEntity(DriverDTO driverDTO) {
        if ( driverDTO == null ) {
            return null;
        }

        Driver driver = new Driver();

        driver.setUsername( driverDTO.getUsername() );
        driver.setName( driverDTO.getName() );
        driver.setPassword( driverDTO.getPassword() );
        driver.setPhone( driverDTO.getPhone() );

        return driver;
    }

    @Override
    public DriverDTO toDTO(Driver driver) {
        if ( driver == null ) {
            return null;
        }

        DriverDTO driverDTO = new DriverDTO();

        driverDTO.setUsername( driver.getUsername() );
        driverDTO.setName( driver.getName() );
        driverDTO.setPassword( driver.getPassword() );
        driverDTO.setPhone( driver.getPhone() );

        return driverDTO;
    }
}
