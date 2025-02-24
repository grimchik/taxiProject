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
import jakarta.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Transactional
    public String changePasswordById(Long id, PasswordDTO password) {
        User user = findActiveUserById(id);
        if (passwordEncoder.matches(password.getPassword(), user.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }

        user.setPassword(passwordEncoder.encode(password.getPassword()));
        userRepository.save(user);
        return "Password changed successfully";
    }

    @Transactional
    public String changePhoneById(Long id, PhoneDTO phone) {
        User user = findActiveUserById(id);

        if (userRepository.findByPhone(phone.getPhone()).isPresent()) {
            throw new EntityExistsException("User with the same phone already exists");
        }

        user.setPhone(phone.getPhone());
        userRepository.save(user);
        return "Phone changed successfully";
    }

    @Transactional
    public String changeUsernameById(Long id, UsernameDTO username)
    {
        User user = findActiveUserById(id);
        if (userRepository.findByUsername(username.getUsername()).isPresent())
        {
            throw new EntityExistsException("User with the same username already exists");
        }
        user.setUsername(username.getUsername());
        userRepository.save(user);
        return "Username changed successfully";
    }

    public UserWithIdDTO getUserProfile(String username)
    {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userWithIdMapper.toDTO(user);
    }

    @Transactional
    public UserWithoutPasswordDTO updateProfile(Long id,UserDTO userDTO)
    {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty())
        {
            throw new EntityNotFoundException("User with " + userDTO.getUsername() + " not found");
        }
        findActiveUserById(id);
        User user= userOptional.get();
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
}