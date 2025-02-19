package clientservice.service;

import clientservice.dto.UserDTO;
import clientservice.dto.UserWithUsernameAndPasswordDTO;
import clientservice.dto.UserWithUsernameAndPhoneDTO;
import clientservice.dto.UserWithoutPasswordDTO;
import clientservice.entity.User;
import clientservice.exception.SamePasswordException;
import clientservice.mapper.UserMapper;
import clientservice.mapper.UserWithoutPasswordMapper;
import clientservice.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserWithoutPasswordMapper userWithoutPasswordMapper = UserWithoutPasswordMapper.INSTANCE;
    public String createUser(UserDTO userDTO) throws EntityExistsException {
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("User with the same username already exists");
        }
        if (userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("User with the same phone already exists");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    public void deleteUser(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with " + username + " not found");
        }
        User user = userOptional.get();
        if (user.getIsDeleted()) {
            throw new IllegalStateException("User with " + username + " has already been deleted");
        }
        userRepository.softDeleteByUsername(username);
    }

    private User findActiveUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User with " + username + " not found");
        }
        User user = userOptional.get();
        if (user.getIsDeleted()) {
            throw new IllegalStateException("User has already been deleted");
        }
        return user;
    }

    @Transactional
    public String changePasswordByUsername(UserWithUsernameAndPasswordDTO userDTO) {
        User user = findActiveUserByUsername(userDTO.getUsername());

        if (passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Transactional
    public String changePhoneByUsername(UserWithUsernameAndPhoneDTO userDTO) {
        User user = findActiveUserByUsername(userDTO.getUsername());

        if (userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("User with the same phone already exists");
        }

        user.setPhone(userDTO.getPhone());
        userRepository.save(user);
        return "Phone changed successfully";
    }

    public UserWithoutPasswordDTO getUserProfileById(Long Id)
    {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userWithoutPasswordMapper.toDTO(user);
    }
}