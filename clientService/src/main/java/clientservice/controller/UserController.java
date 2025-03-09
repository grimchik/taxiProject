package clientservice.controller;

import clientservice.dto.*;
import clientservice.kafkaservice.CancelRideProducer;
import clientservice.kafkaservice.GetRideProducer;
import clientservice.kafkaservice.RideRequestProducer;
import clientservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    private final RideRequestProducer rideRequestProducer;
    private final GetRideProducer getRideProducer;
    private final CancelRideProducer cancelRideProducer;

    public UserController(UserService userService, RideRequestProducer rideRequestProducer, GetRideProducer getRideProducer, CancelRideProducer cancelRideProducer) {
        this.getRideProducer = getRideProducer;
        this.rideRequestProducer = rideRequestProducer;
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
    public ResponseEntity<?> createRide(@RequestBody CreateRideRequestDTO createRideRequest) throws Exception {
        return new ResponseEntity<>(rideRequestProducer.sendRideRequest(createRideRequest), HttpStatus.CREATED);
    }

    @GetMapping("/all-rides/{id}")
    public ResponseEntity<?> getRides(
            @PathVariable("id") Long id,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "5") Integer size) throws Exception {
        GetRidesRequestDTO getRidesRequestDTO = new GetRidesRequestDTO(id, page, size);
        return new ResponseEntity<>(getRideProducer.sendRideRequest(getRidesRequestDTO), HttpStatus.OK);
    }

    @PostMapping("/cancel-ride/{id}")
    public ResponseEntity<?> cancelRide(
            @PathVariable("id") Long rideId,
            @RequestParam(value = "userId") Long userId) throws Exception {
        CanceledRideDTO canceledRideDTO= new CanceledRideDTO(userId,rideId);
        return new ResponseEntity<>(cancelRideProducer.sendCancelRequest(canceledRideDTO),HttpStatus.OK);
    }
}