package clientservice.mapper;
import clientservice.dto.UserWithoutPasswordDTO;
import clientservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserWithoutPasswordMapper {
    UserWithoutPasswordMapper INSTANCE= Mappers.getMapper(UserWithoutPasswordMapper.class);
    @Mapping(target = "username", source = "username")
    User toEntity(UserWithoutPasswordDTO userWithoutPasswordDTO);
    @Mapping(target = "username", source = "username")
    UserWithoutPasswordDTO toDTO(User user);
}