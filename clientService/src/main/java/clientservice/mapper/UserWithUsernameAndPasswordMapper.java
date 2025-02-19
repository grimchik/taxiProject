package clientservice.mapper;

import clientservice.dto.UserWithUsernameAndPasswordDTO;
import clientservice.dto.UserWithoutPasswordDTO;
import clientservice.entity.User;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

public interface UserWithUsernameAndPasswordMapper {
    UserWithUsernameAndPasswordMapper INSTANCE = Mappers.getMapper(UserWithUsernameAndPasswordMapper.class);
    @Mapping(target = "username", source = "username")
    User toEntity(UserWithUsernameAndPasswordDTO userWithUsernameAndPasswordDTO);
    @Mapping(target = "username", source = "username")
    UserWithUsernameAndPasswordDTO toDTO(User user);
}
