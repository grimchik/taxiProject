package clientservice.unittest;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import clientservice.client.*;
import clientservice.dto.*;
import clientservice.entity.User;
import clientservice.mapper.UserMapper;
import clientservice.mapper.UserWithIdMapper;
import clientservice.mapper.UserWithoutPasswordMapper;
import clientservice.repository.UserRepository;
import clientservice.service.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserWithIdMapper userWithIdMapper;

    @Mock
    private UserWithoutPasswordMapper userWithoutPasswordMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("OLEG");
        userDTO.setPhone("+375(29)123-45-67");
        userDTO.setPassword("password123");

        User user = new User();
        user.setUsername("OLEG");

        UserWithIdDTO userWithIdDTO = new UserWithIdDTO();

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(userDTO.getPhone())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDTO)).thenReturn(user);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encryptedPassword123");
        when(userWithIdMapper.toDTO(user)).thenReturn(userWithIdDTO);

        UserWithIdDTO result = userService.createUser(userDTO);

        assertNotNull(result);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("OLEG", savedUser.getUsername());
        assertEquals("+375(29)123-45-67", savedUser.getPhone());
        assertEquals("encryptedPassword123", savedUser.getPassword());
    }


    @Test
    void testCreateUser_UsernameExists() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("OLEG");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(EntityExistsException.class, () -> userService.createUser(userDTO));
    }

    @Test
    void testDeleteUserById_Success() {
        Long userId = 1L;

        User user = new User();
        user.setUsername("OLEG");
        user.setIsDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository, times(1)).softDeleteByUsername(user.getUsername());
    }

    @Test
    void testDeleteUserById_AlreadyDeleted() {
        Long userId = 1L;

        User user = new User();
        user.setIsDeleted(true);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () -> userService.deleteUserById(userId));
    }

    @Test
    void testGetUserProfile_Success() {
        Long userId = 1L;
        User user = new User();
        UserWithIdDTO userWithIdDTO = new UserWithIdDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userWithIdMapper.toDTO(user)).thenReturn(userWithIdDTO);

        UserWithIdDTO result = userService.getUserProfile(userId);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserProfile_NotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserProfile(userId));
    }

    @Test
    void testUpdateProfile_Success() {
        Long userId = 1L;

        User user = new User();
        user.setUsername("OLEG");
        user.setPhone("+375(29)123-45-67");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("new_username");
        userDTO.setPhone("+375(29)987-65-43");
        userDTO.setPassword("new_password");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByPhone(userDTO.getPhone())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encrypted_new_password");
        when(userWithoutPasswordMapper.toDTO(user)).thenReturn(new UserWithoutPasswordDTO());

        UserWithoutPasswordDTO result = userService.updateProfile(userId, userDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
        assertEquals("new_username", user.getUsername());
        assertEquals("+375(29)987-65-43", user.getPhone());
    }

    @Test
    void testUpdateProfile_UsernameExists() {
        Long userId = 1L;

        User user = new User();
        user.setUsername("OLEG");

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("existing_username");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(new User()));

        assertThrows(EntityExistsException.class, () -> userService.updateProfile(userId, userDTO));
    }
}
