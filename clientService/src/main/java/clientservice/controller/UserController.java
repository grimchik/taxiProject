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
    public ResponseEntity<?> deleteUserByUsername(@PathVariable("username") String username)
    {
        userService.deleteUserByUsername(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long Id)
    {
        userService.deleteUserById(Id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/change-password/{id}")
    public ResponseEntity<String> changePassword(@RequestBody String password,@PathVariable("id") Long Id)
    {
        return new ResponseEntity<>(userService.changePasswordById(Id,password),HttpStatus.OK);
    }

    @PatchMapping("/change-phone/{id}")
    public ResponseEntity<String> changePhone(@RequestBody String phone,@PathVariable("id") Long Id)
    {
        return new ResponseEntity<>(userService.changePhoneById(Id,phone),HttpStatus.OK);
    }

    @PatchMapping("/change-username/{id}")
    public ResponseEntity<String> changeUsername(@RequestBody String username,@PathVariable("id") Long Id)
    {
        return new ResponseEntity<>(userService.changeUsernameById(Id,username),HttpStatus.OK);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(userService.getUserProfileById(id),HttpStatus.OK);
    }

    @PutMapping("/change-profile/{id}")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserDTO userDTO,@PathVariable("id") Long Id)
    {
        return new ResponseEntity<>(userService.updateProfile(Id,userDTO),HttpStatus.OK);
    }
}