package driverservice.controller;

import driverservice.dto.*;
import driverservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driver/")
public class DriverController {
    private final DriverService driverService;

    public DriverController(DriverService driverService)
    {
        this.driverService=driverService;
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<?> getProfileById(@PathVariable("username") String username)
    {
        return new ResponseEntity<>(driverService.getDriverProfile(username),HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> createDriver( @Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.createDriver(driverDTO),HttpStatus.CREATED);
    }

    @PutMapping("/change-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,@Valid @RequestBody DriverDTO driverDTO)
    {
        return new ResponseEntity<>(driverService.updateProfile(id,driverDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id,@Valid @RequestBody PasswordDTO passwordDTO)
    {
        return new ResponseEntity<>(driverService.changePasswordById(id,passwordDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-username/{id}")
    public ResponseEntity<?> changeUsername(@PathVariable("id") Long id,@Valid @RequestBody UsernameDTO usernameDTO)
    {
        return new ResponseEntity<>(driverService.changeUsernameById(id,usernameDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-phone/{id}")
    public ResponseEntity<?> changePhone(@PathVariable("id") Long id,@Valid @RequestBody PhoneDTO phoneDTO)
    {
        return new ResponseEntity<>(driverService.changePhoneById(id,phoneDTO),HttpStatus.OK);
    }

    @PatchMapping("/change-name/{id}")
    public ResponseEntity<?> changeName(@PathVariable("id") Long id,@Valid @RequestBody NameDTO nameDTO)
    {
        return new ResponseEntity<>(driverService.changeNameById(id,nameDTO),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDriver(@PathVariable("id") Long id)
    {
        driverService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
