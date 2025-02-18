package clientservice.mapper;
import clientservice.dto.UserWithoutPasswordDTO;
import clientservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWithoutPasswordMapper {
    UserWithoutPasswordMapper INSTANCE= Mappers.getMapper(UserWithoutPasswordMapper.class);
    User toEntity(UserWithoutPasswordDTO userWithoutPasswordDTO);
    UserWithoutPasswordDTO toDTO(User user);
}