package rateservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import rateservice.dto.ClientFeedbackDTO;
import rateservice.entity.ClientFeedback;

@Mapper
public interface ClientFeedbackMapper {
    ClientFeedbackMapper INSTANCE = Mappers.getMapper(ClientFeedbackMapper.class);
    ClientFeedbackDTO toDTO (ClientFeedback clientFeedback);
    ClientFeedback toEntity (ClientFeedbackDTO clientFeedbackDTO);
}
