package clientservice.controller;

import clientservice.dto.*;
import clientservice.kafkaservice.CancelRideProducer;
import clientservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final CancelRideProducer cancelRideProducer;

    public UserController(UserService userService, CancelRideProducer cancelRideProducer) {
        this.userService = userService;
        this.cancelRideProducer=cancelRideProducer;
    }

    @PostMapping
    public ResponseEntity<?> signUpUser(@RequestBody @Valid UserDTO userDTO) {
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id, @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        return new ResponseEntity<>(userService.changeUser(id, updateUserDTO), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.getUserProfile(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProfiles(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        return new ResponseEntity<>(userService.getAllProfiles(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@RequestBody @Valid UserDTO userDTO, @PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.updateProfile(id, userDTO), HttpStatus.OK);
    }

    @PostMapping("/create-ride")
    public ResponseEntity<?> createRide(@Valid @RequestBody RideDTO rideDTO)
    {
        return new ResponseEntity<>(userService.createRide(rideDTO), HttpStatus.CREATED);
    }

    @GetMapping("/user-rides/{userId}")
    public ResponseEntity<?> getUserRides(@PathVariable("userId") Long userId,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getRidesByUserId(userId, page, size),HttpStatus.OK);
    }

    @PatchMapping("/update-ride/{id}")
    public ResponseEntity<?> updateRide(@PathVariable("id") Long rideId, @RequestBody UpdateRideDTO updateRideDTO)
    {
        return new ResponseEntity<>(userService.changeRide(rideId,updateRideDTO),HttpStatus.OK);
    }

    @PostMapping("/cancel-ride/{rideId}")
    public ResponseEntity<Void> cancelRide(
            @PathVariable("rideId") Long rideId,
            @RequestParam("userId") Long userId)
    {
        CanceledRideDTO canceledRideDTO = new CanceledRideDTO(userId, rideId);
        cancelRideProducer.sendCancelRequest(canceledRideDTO);
        return ResponseEntity.accepted().build();
    }
}