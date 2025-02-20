package driverservice.mapper;

import driverservice.dto.DriverWithoutPasswordDTO;
import driverservice.entity.Driver;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-20T18:23:36+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class DriverWithoutPasswordMapperImpl implements DriverWithoutPasswordMapper {

    @Override
    public DriverWithoutPasswordDTO toDTO(Driver driver) {
        if ( driver == null ) {
            return null;
        }

        DriverWithoutPasswordDTO driverWithoutPasswordDTO = new DriverWithoutPasswordDTO();

        driverWithoutPasswordDTO.setUsername( driver.getUsername() );
        driverWithoutPasswordDTO.setName( driver.getName() );
        driverWithoutPasswordDTO.setPhone( driver.getPhone() );

        return driverWithoutPasswordDTO;
    }

    @Override
    public Driver toEntity(DriverWithoutPasswordDTO driverWithoutPasswordDTO) {
        if ( driverWithoutPasswordDTO == null ) {
            return null;
        }

        Driver driver = new Driver();

        driver.setUsername( driverWithoutPasswordDTO.getUsername() );
        driver.setName( driverWithoutPasswordDTO.getName() );
        driver.setPhone( driverWithoutPasswordDTO.getPhone() );

        return driver;
    }
}
