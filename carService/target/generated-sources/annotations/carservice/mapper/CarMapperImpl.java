package carservice.mapper;

import carservice.dto.CarDTO;
import carservice.entity.Car;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-21T17:04:47+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class CarMapperImpl implements CarMapper {

    @Override
    public Car toEntity(CarDTO carDTO) {
        if ( carDTO == null ) {
            return null;
        }

        Car car = new Car();

        car.setNumber( carDTO.getNumber() );
        car.setBrand( carDTO.getBrand() );
        car.setModel( carDTO.getModel() );
        car.setDescription( carDTO.getDescription() );
        car.setColor( carDTO.getColor() );
        car.setCategory( carDTO.getCategory() );

        return car;
    }

    @Override
    public CarDTO toDTO(Car car) {
        if ( car == null ) {
            return null;
        }

        CarDTO carDTO = new CarDTO();

        carDTO.setNumber( car.getNumber() );
        carDTO.setBrand( car.getBrand() );
        carDTO.setModel( car.getModel() );
        carDTO.setDescription( car.getDescription() );
        carDTO.setColor( car.getColor() );
        carDTO.setCategory( car.getCategory() );

        return carDTO;
    }
}
