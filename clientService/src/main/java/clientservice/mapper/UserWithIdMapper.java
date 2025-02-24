package clientservice.mapper;

import clientservice.dto.UserWithIdDTO;
import clientservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWithIdMapper {
    UserWithIdMapper INSTANCE= Mappers.getMapper(UserWithIdMapper.class);
    @Mapping(source = "id",target = "id")
    UserWithIdDTO toDTO(User user);
    @Mapping(source = "id",target = "id")
    User toEntity(UserWithIdDTO userWithIdDTO);
}