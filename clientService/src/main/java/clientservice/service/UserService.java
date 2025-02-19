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
import jakarta.persistence.Id;
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

    public void deleteUserByUsername(String username) {
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

    public void deleteUserById(Long Id) {
        Optional<User> userOptional = userRepository.findById(Id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOptional.get();
        if (user.getIsDeleted()) {
            throw new IllegalStateException("User with " + user.getUsername() + " has already been deleted");
        }
        userRepository.softDeleteByUsername(user.getUsername());
    }

    private User findActiveUserById(Long Id) {
        Optional<User> userOptional = userRepository.findById(Id);
        if (userOptional.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        User user = userOptional.get();
        if (user.getIsDeleted()) {
            throw new IllegalStateException("User has already been deleted");
        }
        return user;
    }

    @Transactional
    public String changePasswordById(Long Id, String password) {
        User user = findActiveUserById(Id);

        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Transactional
    public String changePhoneById(Long Id, String phone) {
        User user = findActiveUserById(Id);

        if (userRepository.findByPhone(phone).isPresent()) {
            throw new EntityExistsException("User with the same phone already exists");
        }

        user.setPhone(phone);
        userRepository.save(user);
        return "Phone changed successfully";
    }

    @Transactional
    public String changeUsernameById(Long Id,String username)
    {
        User user = findActiveUserById(Id);
        if (userRepository.findByUsername(username).isPresent())
        {
            throw new EntityExistsException("User with the same username already exists");
        }
        user.setUsername(username);
        return "Username changed successfully";
    }

    public UserWithoutPasswordDTO getUserProfileById(Long Id)
    {
        User user = userRepository.findById(Id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userWithoutPasswordMapper.toDTO(user);
    }

    @Transactional
    public UserWithoutPasswordDTO updateProfile(Long Id,UserDTO userDTO)
    {
        Optional<User> userOptional = userRepository.findById(Id);
        if(userOptional.isEmpty())
        {
            throw new EntityNotFoundException("User with " + userDTO.getUsername() + " not found");
        }
        findActiveUserById(Id);
        User user= userOptional.get();
        if (userRepository.findByUsername(user.getUsername()).isPresent() || userRepository.findByPhone(user.getPhone()).isPresent())
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
}