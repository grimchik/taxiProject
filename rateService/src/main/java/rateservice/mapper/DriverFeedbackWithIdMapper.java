package rateservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import rateservice.dto.DriverFeedbackDTO;
import rateservice.entity.DriverFeedback;

@Mapper
public interface DriverFeedbackWithIdMapper {
    DriverFeedbackWithIdMapper INSTANCE = Mappers.getMapper(DriverFeedbackWithIdMapper.class);
    @Mapping(target = "id",source = "id")
    DriverFeedbackDTO toDTO(DriverFeedback driverFeedback);
    @Mapping(target = "id",source = "id")
    DriverFeedback toEntity(DriverFeedbackDTO driverFeedbackDTO);
}
