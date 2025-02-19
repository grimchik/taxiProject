package clientservice.mapper;

import clientservice.dto.UserWithUsernameAndPhoneDTO;
import clientservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWithUsernameAndPhoneMapper {
    UserWithUsernameAndPhoneMapper INSTANCE = Mappers.getMapper(UserWithUsernameAndPhoneMapper.class);

    User toEntity(UserWithUsernameAndPhoneDTO dto);

    UserWithUsernameAndPhoneDTO toDTO(User user);
}
