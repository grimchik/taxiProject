package clientservice.controller;

import clientservice.dto.UserDTO;
import clientservice.dto.UserWithUsernameAndPasswordDTO;
import clientservice.dto.UserWithUsernameAndPhoneDTO;
import clientservice.dto.UserWithoutPasswordDTO;
import clientservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody UserDTO userDTO)
    {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username)
    {
        userService.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody UserWithUsernameAndPasswordDTO userWithUsernameAndPasswordDTO)
    {
        return new ResponseEntity<>(userService.changePasswordByUsername(userWithUsernameAndPasswordDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-phone")
    public ResponseEntity<String> changePhone(@Valid @RequestBody UserWithUsernameAndPhoneDTO userWithUsernameAndPasswordDTO)
    {
        return new ResponseEntity<>(userService.changePhoneByUsername(userWithUsernameAndPasswordDTO),HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(userService.getUserProfileById(id),HttpStatus.OK);
    }
}