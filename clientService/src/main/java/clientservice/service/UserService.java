package clientservice.service;

import clientservice.dto.*;
import clientservice.entity.User;
import clientservice.exception.SamePasswordException;
import clientservice.mapper.UserMapper;
import clientservice.mapper.UserWithIdMapper;
import clientservice.mapper.UserWithoutPasswordMapper;
import clientservice.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserWithoutPasswordMapper userWithoutPasswordMapper = UserWithoutPasswordMapper.INSTANCE;
    private final UserWithIdMapper userWithIdMapper = UserWithIdMapper.INSTANCE;
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserWithIdDTO createUser(UserDTO userDTO) throws EntityExistsException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("User with the same username already exists");
        }
        if (userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("User with the same phone already exists");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        return userWithIdMapper.toDTO(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOptional.get();
        if (user.getIsDeleted()) {
            throw new IllegalStateException("User with " + user.getUsername() + " has already been deleted");
        }
        userRepository.softDeleteByUsername(user.getUsername());
    }

    private User findActiveUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOptional.get();
        if (user.getIsDeleted()) {
            throw new IllegalStateException("User has already been deleted");
        }
        return user;
    }

    public UserWithIdDTO getUserProfile(Long id)
    {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userWithIdMapper.toDTO(user);
    }

    public Page<UserWithIdDTO> getAllProfiles(Pageable pageable) {
        return userRepository.findAll(pageable).map(userWithIdMapper::toDTO);
    }
    
    @Transactional
    public UserWithoutPasswordDTO updateProfile(Long id,UserDTO userDTO)
    {
        User user= findActiveUserById(id);
        if ((userRepository.findByUsername(userDTO.getUsername()).isPresent() && !userDTO.getUsername().equals(user.getUsername()))|| (userRepository.findByPhone(userDTO.getPhone()).isPresent() && !userDTO.getPhone().equals(user.getPhone())))
        {
            throw new EntityExistsException("User with this username or phone already exists");
        }
        if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPhone(userDTO.getPhone());
        userRepository.save(user);
        return userWithoutPasswordMapper.toDTO(user);
    }

    @Transactional
    public UserWithIdDTO changeUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = findActiveUserById(id);

        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().isBlank()) {
            if (userRepository.findByUsername(updateUserDTO.getUsername()).isPresent() &&
                    !updateUserDTO.getUsername().equals(user.getUsername())) {
                throw new EntityExistsException("User with the same username already exists");
            }
            user.setUsername(updateUserDTO.getUsername());
        }

        if (updateUserDTO.getPhone() != null && !updateUserDTO.getPhone().isBlank()) {
            if (userRepository.findByPhone(updateUserDTO.getPhone()).isPresent() &&
                    !updateUserDTO.getPhone().equals(user.getPhone())) {
                throw new EntityExistsException("User with the same phone already exists");
            }
            user.setPhone(updateUserDTO.getPhone());
        }

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isBlank()) {
            if (passwordEncoder.matches(updateUserDTO.getPassword(), user.getPassword())) {
                throw new SamePasswordException("The new password cannot be the same as the old password.");
            }
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        userRepository.save(user);
        return userWithIdMapper.toDTO(user);
    }

}