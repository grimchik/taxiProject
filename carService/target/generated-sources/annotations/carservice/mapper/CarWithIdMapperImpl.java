package carservice.mapper;

import carservice.dto.CarWithIdDTO;
import carservice.entity.Car;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-21T17:04:47+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class CarWithIdMapperImpl implements CarWithIdMapper {

    @Override
    public Car toEntity(CarWithIdDTO carWithIdDTO) {
        if ( carWithIdDTO == null ) {
            return null;
        }

        Car car = new Car();

        car.setNumber( carWithIdDTO.getNumber() );
        car.setId( carWithIdDTO.getId() );
        car.setBrand( carWithIdDTO.getBrand() );
        car.setModel( carWithIdDTO.getModel() );
        car.setDescription( carWithIdDTO.getDescription() );
        car.setColor( carWithIdDTO.getColor() );
        car.setCategory( carWithIdDTO.getCategory() );

        return car;
    }

    @Override
    public CarWithIdDTO toDTO(Car car) {
        if ( car == null ) {
            return null;
        }

        CarWithIdDTO carWithIdDTO = new CarWithIdDTO();

        carWithIdDTO.setNumber( car.getNumber() );
        carWithIdDTO.setId( car.getId() );
        carWithIdDTO.setBrand( car.getBrand() );
        carWithIdDTO.setModel( car.getModel() );
        carWithIdDTO.setDescription( car.getDescription() );
        carWithIdDTO.setColor( car.getColor() );
        carWithIdDTO.setCategory( car.getCategory() );

        return carWithIdDTO;
    }
}
