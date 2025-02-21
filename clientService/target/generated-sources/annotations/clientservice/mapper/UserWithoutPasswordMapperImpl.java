package clientservice.mapper;

import clientservice.dto.UserWithoutPasswordDTO;
import clientservice.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-20T10:54:15+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class UserWithoutPasswordMapperImpl implements UserWithoutPasswordMapper {

    @Override
    public User toEntity(UserWithoutPasswordDTO userWithoutPasswordDTO) {
        if ( userWithoutPasswordDTO == null ) {
            return null;
        }

        User user = new User();

        user.setUsername( userWithoutPasswordDTO.getUsername() );
        user.setPhone( userWithoutPasswordDTO.getPhone() );

        return user;
    }

    @Override
    public UserWithoutPasswordDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserWithoutPasswordDTO userWithoutPasswordDTO = new UserWithoutPasswordDTO();

        userWithoutPasswordDTO.setUsername( user.getUsername() );
        userWithoutPasswordDTO.setPhone( user.getPhone() );

        return userWithoutPasswordDTO;
    }
}
