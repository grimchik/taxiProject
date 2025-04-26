package clientservice.controller;

import clientservice.dto.*;
import clientservice.kafkaservice.CancelRideProducer;
import clientservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/users/")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> signUpUser(@RequestBody @Valid UserDTO userDTO) {
        log.info("POST /users - Signing up new user with phone: {}", userDTO.getPhone());
        return new ResponseEntity<>(userService.createUser(userDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") Long id) {
        log.info("DELETE /users - Deleting user with ID: {}", id);
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id,
                                            @RequestBody @Valid UpdateUserDTO updateUserDTO) {
        log.info("PATCH /users - Changing password for user with ID: {}", id);
        return new ResponseEntity<>(userService.changeUser(id, updateUserDTO), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id) {
        log.info("GET /users - Retrieving user profile by ID: {}", id);
        return new ResponseEntity<>(userService.getUserProfile(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProfiles(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /users - Retrieving all users, page={}, size={}", page, size);
        return new ResponseEntity<>(userService.getAllProfiles(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,
                                           @RequestBody @Valid UserDTO userDTO) {
        log.info("PUT /users - Updating user with ID: {}", id);
        return new ResponseEntity<>(userService.updateProfile(id, userDTO), HttpStatus.OK);
    }

    @PostMapping("/create-ride/{userId}")
    public ResponseEntity<?> createRide(@PathVariable("userId") Long userId,
                                        @RequestBody RideDTO rideDTO) {
        log.info("POST /users/create-ride - Creating ride for user ID: {}", userId);
        return new ResponseEntity<>(userService.createRide(userId, rideDTO), HttpStatus.CREATED);
    }

    @GetMapping("/user-rides/{userId}")
    public ResponseEntity<?> getUserRides(@PathVariable("userId") Long userId,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /users/user-rides - Getting rides for user ID: {}, page={}, size={}", userId, page, size);
        return new ResponseEntity<>(userService.getRidesByUserId(userId, page, size), HttpStatus.OK);
    }

    @PatchMapping("{userId}/update-ride/{rideId}")
    public ResponseEntity<?> updateRide(@PathVariable("userId") Long userId,
                                        @PathVariable("rideId") Long rideId,
                                        @RequestBody UpdateRideDTO updateRideDTO) {
        log.info("PATCH /users/update-ride - Updating ride ID: {} for user ID: {}", rideId, userId);
        return new ResponseEntity<>(userService.changeRide(rideId, userId, updateRideDTO), HttpStatus.OK);
    }

    @PostMapping("{userId}/cancel-ride/{rideId}")
    public ResponseEntity<?> cancelRide(@PathVariable("rideId") Long rideId,
                                        @PathVariable("userId") Long userId) {
        log.info("POST /users/cancel-ride - Cancelling ride ID: {} for user ID: {}", rideId, userId);
        CanceledRideDTO canceledRideDTO = new CanceledRideDTO(userId, rideId);
        userService.cancelRide(canceledRideDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/apply-promocode")
    public ResponseEntity<?> applyPromocode(@Valid @RequestBody CheckPromoCodeDTO checkPromoCodeDTO) {
        log.info("POST /users/apply-promocode - Applying promocode: {}", checkPromoCodeDTO.getKeyword());
        userService.checkPromoCode(checkPromoCodeDTO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/{userId}/create-feedback")
    public ResponseEntity<?> createFeedback(@PathVariable("userId") Long userId,
                                            @RequestBody ClientFeedbackDTO clientFeedbackDTO) {
        log.info("POST /users/create-feedback - Creating feedback for user ID: {}", userId);
        return new ResponseEntity<>(userService.createFeedback(userId, clientFeedbackDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/user-feedbacks")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("userId") Long userId,
                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /users/user-feedbacks - Getting feedbacks for user ID: {}, page={}, size={}", userId, page, size);
        return new ResponseEntity<>(userService.getAllFeedbacks(userId, page, size), HttpStatus.OK);
    }

    @GetMapping("/{userId}/user-rate")
    public ResponseEntity<?> getUserRate(@PathVariable("userId") Long userId) {
        log.info("GET /users/user-rate - Getting rate for user ID: {}", userId);
        return new ResponseEntity<>(userService.getUserRate(userId), HttpStatus.OK);
    }

    @PatchMapping("/update-feedback/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable("feedbackId") Long feedbackId,
                                            @RequestBody UpdateClientRateDTO updateClientRateDTO) {
        log.info("PATCH /users/update-feedback - Updating feedback ID: {}", feedbackId);
        return new ResponseEntity<>(userService.changeFeedback(feedbackId, updateClientRateDTO), HttpStatus.OK);
    }

    @GetMapping("/{userId}/payments")
    public ResponseEntity<?> getAllPaymentsByUser(@PathVariable("userId") Long userId,
                                                  @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /users/payments - Getting all payments for user ID: {}, page={}, size={}", userId, page, size);
        return new ResponseEntity<>(userService.getAllPaymentsByUser(userId, page, size), HttpStatus.OK);
    }

    @GetMapping("/{userId}/pending-payments")
    public ResponseEntity<?> getPendingPayment(@PathVariable("userId") Long userId) {
        log.info("GET /users/pending-payments - Getting pending payments for user ID: {}", userId);
        return new ResponseEntity<>(userService.getPendingPaymentByUser(userId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/confirmed-payment/{paymentId}")
    public ResponseEntity<?> confirmedPayment(@PathVariable("userId") Long userId,
                                              @PathVariable("paymentId") Long paymentId,
                                              @Valid @RequestBody ConfirmedPaymentDTO confirmedPaymentDTO) {
        log.info("PATCH /users/confirmed-payment - Confirming payment ID: {} for user ID: {}", paymentId, userId);
        return new ResponseEntity<>(userService.confirmedPayment(userId, paymentId, confirmedPaymentDTO), HttpStatus.OK);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //ADMIN METHODS
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //____________________________________________________
    //CAR METHODS
    //____________________________________________________

    @GetMapping("/all-cars")
    public ResponseEntity<?> getAllCars(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /users/all-cars - Getting all cars, page={}, size={}", page, size);
        return new ResponseEntity<>(userService.getAllCars(page, size), HttpStatus.OK);
    }

    @PostMapping("/create-car")
    public ResponseEntity<?> createCar(@RequestBody CarDTO carCreateDTO) {
        log.info("POST /users/create-car - Creating new car: {}", carCreateDTO.getModel());
        return new ResponseEntity<>(userService.createCar(carCreateDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/change-car/{carId}")
    public ResponseEntity<?> changeCar(@PathVariable("carId") Long carId,
                                       @RequestBody UpdateCarDTO updateCarDTO) {
        log.info("PATCH /users/change-car - Updating car ID: {}", carId);
        return new ResponseEntity<>(userService.changeCar(carId, updateCarDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete-car/{carId}")
    public ResponseEntity<?> deleteCar(@PathVariable("carId") Long carId) {
        log.info("DELETE /users/delete-car - Deleting car ID: {}", carId);
        userService.deleteCar(carId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //____________________________________________________
    //PROMOCODE METHODS
    //____________________________________________________

    @GetMapping("/all-promocodes")
    public ResponseEntity<?> getAllPromoCodes(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("GET /users/all-promocodes - Getting all promocodes, page={}, size={}", page, size);
        return new ResponseEntity<>(userService.getAllPromoCodes(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PostMapping("/create-promocode")
    public ResponseEntity<?> createPromoCode(PromoCodeDTO promoCodeDTO) {
        log.info("POST /users/create-promocode - Creating new promocode: {}", promoCodeDTO.getKeyword());
        return new ResponseEntity<>(userService.createPromoCode(promoCodeDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/change-promocode/{promoCodeId}")
    public ResponseEntity<?> changePromoCode(@PathVariable("promoCodeId") Long promoCodeId,
                                             @RequestBody UpdatePromoCodeDTO updatePromoCodeDTO) {
        log.info("PATCH /users/change-promocode - Updating promocode ID: {}", promoCodeId);
        return new ResponseEntity<>(userService.changePromoCode(promoCodeId, updatePromoCodeDTO), HttpStatus.OK);
    }

    @DeleteMapping("/delete-promocode/{promoCodeId}")
    public ResponseEntity<?> deletePromoCode(@PathVariable("promoCodeId") Long promoCodeId) {
        log.info("DELETE /users/delete-promocode - Deleting promocode ID: {}", promoCodeId);
        userService.deletePromoCode(promoCodeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
