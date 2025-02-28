package rateservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import rateservice.dto.DriverFeedbackDTO;
import rateservice.entity.DriverFeedback;

@Mapper
public interface DriverFeedbackMapper {
    DriverFeedbackMapper INSTANCE = Mappers.getMapper(DriverFeedbackMapper.class);
    DriverFeedbackDTO toDTO (DriverFeedback driverFeedback);
    DriverFeedback toEntity (DriverFeedbackDTO driverFeedbackDTO);
}
