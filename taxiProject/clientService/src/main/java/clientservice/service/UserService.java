package clientservice.service;

import clientservice.dto.UserDTO;
import clientservice.entity.User;
import clientservice.mapper.UserMapper;
import clientservice.mapper.UserWithoutPasswordMapper;
import clientservice.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (userRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("User with " + username + " not found");
        }
        userRepository.softDeleteByUsername(username);
    }
}