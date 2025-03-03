package rateservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import rateservice.dto.DriverFeedbackWithIdDTO;
import rateservice.entity.DriverFeedback;

@Mapper
public interface DriverFeedbackWithIdMapper {
    DriverFeedbackWithIdMapper INSTANCE = Mappers.getMapper(DriverFeedbackWithIdMapper.class);
    @Mapping(target = "id",source = "id")
    DriverFeedbackWithIdDTO toDTO(DriverFeedback driverFeedback);
    @Mapping(target = "id",source = "id")
    DriverFeedback toEntity(DriverFeedbackWithIdDTO driverFeedbackDTO);
}
