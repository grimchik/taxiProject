package clientservice.mapper;

import clientservice.dto.UserDTO;
import clientservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(target = "username",source = "username")
    UserDTO toDTO (User user);
    @Mapping(target = "username", source = "username")
    User toEntity (UserDTO userDTO);
}