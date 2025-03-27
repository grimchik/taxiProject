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
@RequestMapping("/api/v1/users/")
public class UserController {
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
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
    public ResponseEntity<?> changePassword(@PathVariable("id") Long id,
                                            @RequestBody @Valid UpdateUserDTO updateUserDTO)
    {
        return new ResponseEntity<>(userService.changeUser(id, updateUserDTO), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.getUserProfile(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllProfiles(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getAllProfiles(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable("id") Long id,
                                           @RequestBody @Valid UserDTO userDTO)
    {
        return new ResponseEntity<>(userService.updateProfile(id, userDTO), HttpStatus.OK);
    }

    @PostMapping("/create-ride/{userId}")
    public ResponseEntity<?> createRide(@PathVariable("userId") Long userId,
                                        @RequestBody RideDTO rideDTO)
    {
        return new ResponseEntity<>(userService.createRide(userId,rideDTO), HttpStatus.CREATED);
    }

    @GetMapping("/user-rides/{userId}")
    public ResponseEntity<?> getUserRides(@PathVariable("userId") Long userId,
                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                          @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getRidesByUserId(userId, page, size), HttpStatus.OK);
    }

    @PatchMapping("{userId}/update-ride/{rideId}")
    public ResponseEntity<?> updateRide(@PathVariable("userId") Long userId,
                                        @PathVariable("rideId") Long rideId,
                                        @RequestBody UpdateRideDTO updateRideDTO)
    {
        return new ResponseEntity<>(userService.changeRide(rideId,userId,updateRideDTO), HttpStatus.OK);
    }

    @PostMapping("{userId}/cancel-ride/{rideId}")
    public ResponseEntity<?> cancelRide(
            @PathVariable("rideId") Long rideId,
            @PathVariable("userId") Long userId)
    {
        CanceledRideDTO canceledRideDTO = new CanceledRideDTO(userId, rideId);
        userService.cancelRide(canceledRideDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/apply-promocode")
    public ResponseEntity<?> applyPromocode (@Valid @RequestBody CheckPromoCodeDTO checkPromoCodeDTO)
    {
        userService.checkPromoCode(checkPromoCodeDTO);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/{userId}/create-feedback")
    public ResponseEntity<?> createFeedback (@PathVariable("userId") Long userId,
                                             @RequestBody ClientFeedbackDTO clientFeedbackDTO)
    {
        return new ResponseEntity<>(userService.createFeedback(userId,clientFeedbackDTO),HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/user-feedbacks")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("userId") Long userId,
                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getAllFeedbacks(userId,page,size),HttpStatus.OK);
    }

    @GetMapping("/{userId}/user-rate")
    public ResponseEntity<?> getUserFeedbacks(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(userService.getUserRate(userId),HttpStatus.OK);
    }

    @PatchMapping("/update-feedback/{feedbackId}")
    public ResponseEntity<?> updateFeedback(@PathVariable("feedbackId") Long feedbackId,
                                            @RequestBody UpdateClientRateDTO updateClientRateDTO)
    {
        return new ResponseEntity<>(userService.changeFeedback(feedbackId, updateClientRateDTO), HttpStatus.OK);
    }

    @GetMapping("/{userId}/payments")
    public ResponseEntity<?> getAllPaymentsByUser (@PathVariable("userId") Long userId,
                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                   @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getAllPaymentsByUser(userId,page,size),HttpStatus.OK);
    }

    @GetMapping("/{userId}/pending-payments")
    public ResponseEntity<?> getPendingPayment(@PathVariable("userId") Long userId)
    {
        return new ResponseEntity<>(userService.getPendingPaymentByUser(userId),HttpStatus.OK);
    }

    @PatchMapping("/{userId}/confirmed-payment/{paymentId}")
    public ResponseEntity<?> confirmedPayment(@PathVariable("userId") Long userId,
                                              @PathVariable("paymentId") Long paymentId,
                                              @Valid @RequestBody ConfirmedPaymentDTO confirmedPaymentDTO)
    {
        return new ResponseEntity<>(userService.confirmedPayment(userId, paymentId, confirmedPaymentDTO),HttpStatus.OK);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //ADMIN METHODS
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    //____________________________________________________
    //CAR METHODS
    //____________________________________________________
    @GetMapping("/all-cars")
    public ResponseEntity<?> getAllCars(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                        @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getAllCars(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PostMapping("/create-car")
    public ResponseEntity<?> createCar (CarCreateDTO carCreateDTO)
    {
        return new ResponseEntity<>(userService.createCar(carCreateDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/change-car/{carId}")
    public ResponseEntity<?> changeCar (@PathVariable("carId") Long carId,
                                        @RequestBody UpdateCarDTO updateCarDTO)
    {
        return new ResponseEntity<>(userService.changeCar(carId,updateCarDTO),HttpStatus.OK);
    }

    @DeleteMapping("/delete-car/{carId}")
    public ResponseEntity<?> deleteCar (@PathVariable("carId") Long carId)
    {
        userService.deleteCar(carId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //____________________________________________________
    //PROMO CODES METHODS
    //____________________________________________________

    @GetMapping("/all-promocodes")
    public ResponseEntity<?> getAllPromoCodes(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "5") Integer size)
    {
        return new ResponseEntity<>(userService.getAllPromoCodes(PageRequest.of(page, size)), HttpStatus.OK);
    }

    @PostMapping("/create-promocode")
    public ResponseEntity<?> createPromoCode (PromoCodeDTO promoCodeDTO)
    {
        return new ResponseEntity<>(userService.createPromoCode(promoCodeDTO), HttpStatus.CREATED);
    }

    @PatchMapping("/change-promocode/{promoCodeId}")
    public ResponseEntity<?> changePromoCode (@PathVariable("promoCodeId") Long promoCodeId,
                                              @RequestBody UpdatePromoCodeDTO updatePromoCodeDTO)
    {
        return new ResponseEntity<>(userService.changePromoCode(promoCodeId,updatePromoCodeDTO),HttpStatus.OK);
    }

    @DeleteMapping("/delete-promocode/{promoCodeId}")
    public ResponseEntity<?> deletePromoCode (@PathVariable("promoCodeId") Long promoCodeId)
    {
        userService.deletePromoCode(promoCodeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}