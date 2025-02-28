package clientservice.controller;

import clientservice.dto.*;
import clientservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    public UserController (UserService userService)
    {
        this.userService=userService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUpUser(@RequestBody @Valid UserDTO userDTO)
    {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id)
    {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/change-password/{id}")
    public ResponseEntity<String> changePassword(@RequestBody @Valid PasswordDTO password, @PathVariable("id") Long id)
    {
        return new ResponseEntity<>(userService.changePasswordById(id,password),HttpStatus.OK);
    }

    @PatchMapping("/change-phone/{id}")
    public ResponseEntity<String> changePhone(@RequestBody @Valid PhoneDTO phone, @PathVariable("id") Long id)
    {
        return new ResponseEntity<>(userService.changePhoneById(id,phone),HttpStatus.OK);
    }

    @PatchMapping("/change-username/{id}")
    public ResponseEntity<String> changeUsername(@RequestBody @Valid UsernameDTO username, @PathVariable("id") Long id)
    {
        return new ResponseEntity<>(userService.changeUsernameById(id,username),HttpStatus.OK);
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfile(@PathVariable("username") String username)
    {
        return new ResponseEntity<>(userService.getUserProfile(username),HttpStatus.OK);
    }

    @PutMapping("/change-profile/{id}")
    public ResponseEntity<?> updateProfile(@RequestBody @Valid UserDTO userDTO,@PathVariable("id") Long id)
    {
        return new ResponseEntity<>(userService.updateProfile(id,userDTO),HttpStatus.OK);
    }
}