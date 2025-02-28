package rateservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import rateservice.dto.ClientFeedbackWithIdDTO;
import rateservice.entity.ClientFeedback;

@Mapper
public interface ClientFeedbackWithIdMapper {
    ClientFeedbackWithIdMapper INSTANCE = Mappers.getMapper(ClientFeedbackWithIdMapper.class);
    @Mapping(target = "id",source = "id")
    ClientFeedbackWithIdDTO toDTO (ClientFeedback clientFeedback);
    @Mapping(target = "id",source = "id")
    ClientFeedback toEntity (ClientFeedbackWithIdDTO clientFeedbackWithIdDTO);
}
