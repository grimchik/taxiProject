package clientservice.mapper;

import clientservice.dto.UserWithIdDTO;
import clientservice.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-20T19:39:35+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class UserWithIdMapperImpl implements UserWithIdMapper {

    @Override
    public UserWithIdDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserWithIdDTO userWithIdDTO = new UserWithIdDTO();

        userWithIdDTO.setId( user.getId() );
        userWithIdDTO.setUsername( user.getUsername() );
        userWithIdDTO.setPhone( user.getPhone() );

        return userWithIdDTO;
    }

    @Override
    public User toEntity(UserWithIdDTO userWithIdDTO) {
        if ( userWithIdDTO == null ) {
            return null;
        }

        User user = new User();

        user.setId( userWithIdDTO.getId() );
        user.setUsername( userWithIdDTO.getUsername() );
        user.setPhone( userWithIdDTO.getPhone() );

        return user;
    }
}
