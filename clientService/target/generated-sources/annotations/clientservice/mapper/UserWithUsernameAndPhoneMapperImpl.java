package clientservice.mapper;

import clientservice.entity.User;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-19T17:48:42+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
public class UserWithUsernameAndPhoneMapperImpl implements UserWithUsernameAndPhoneMapper {

    @Override
    public User toEntity(UserWithUsernameAndPhoneDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        return user;
    }

    @Override
    public UserWithUsernameAndPhoneDTO toDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserWithUsernameAndPhoneDTO userWithUsernameAndPhoneDTO = new UserWithUsernameAndPhoneDTO();

        return userWithUsernameAndPhoneDTO;
    }
}
