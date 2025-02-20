package driverservice.mapper;

import driverservice.dto.DriverWithIdDTO;
import driverservice.entity.Driver;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-20T18:45:40+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class DriverWithIdMapperImpl implements DriverWithIdMapper {

    @Override
    public Driver toEntity(DriverWithIdDTO driverWithIdDTO) {
        if ( driverWithIdDTO == null ) {
            return null;
        }

        Driver driver = new Driver();

        driver.setId( driverWithIdDTO.getId() );
        driver.setName( driverWithIdDTO.getName() );
        driver.setUsername( driverWithIdDTO.getUsername() );
        driver.setPhone( driverWithIdDTO.getPhone() );

        return driver;
    }

    @Override
    public DriverWithIdDTO toDTO(Driver driver) {
        if ( driver == null ) {
            return null;
        }

        DriverWithIdDTO driverWithIdDTO = new DriverWithIdDTO();

        driverWithIdDTO.setId( driver.getId() );
        driverWithIdDTO.setName( driver.getName() );
        driverWithIdDTO.setUsername( driver.getUsername() );
        driverWithIdDTO.setPhone( driver.getPhone() );

        return driverWithIdDTO;
    }
}
