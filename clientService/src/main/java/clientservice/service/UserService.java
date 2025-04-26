package clientservice.service;

import clientservice.client.*;
import clientservice.dto.*;
import clientservice.entity.User;
import clientservice.exception.SamePasswordException;
import clientservice.kafkaservice.CancelRideProducer;
import clientservice.kafkaservice.CheckPromoCodeProducer;
import clientservice.mapper.UserMapper;
import clientservice.mapper.UserWithIdMapper;
import clientservice.mapper.UserWithoutPasswordMapper;
import clientservice.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final UserWithoutPasswordMapper userWithoutPasswordMapper = UserWithoutPasswordMapper.INSTANCE;
    private final UserWithIdMapper userWithIdMapper = UserWithIdMapper.INSTANCE;

    private final CancelRideProducer cancelRideProducer;
    private final CheckPromoCodeProducer checkPromoCodeProducer;

    private final RideServiceClient rideServiceClient;
    private final CarServiceClient carServiceClient;
    private final PromoCodeServiceClient promoCodeServiceClient;
    private final FeedbackServiceClient feedbackServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       RideServiceClient rideServiceClient,CancelRideProducer cancelRideProducer,
                       CarServiceClient carServiceClient,PromoCodeServiceClient promoCodeServiceClient,
                       CheckPromoCodeProducer checkPromoCodeProducer,FeedbackServiceClient feedbackServiceClient,
                       PaymentServiceClient paymentServiceClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rideServiceClient=rideServiceClient;
        this.cancelRideProducer=cancelRideProducer;
        this.carServiceClient=carServiceClient;
        this.promoCodeServiceClient=promoCodeServiceClient;
        this.checkPromoCodeProducer=checkPromoCodeProducer;
        this.feedbackServiceClient=feedbackServiceClient;
        this.paymentServiceClient=paymentServiceClient;
    }

    public UserWithIdDTO createUser(UserDTO userDTO) throws EntityExistsException {
        log.info("Creating new user with phone: {} and username: {}", userDTO.getPhone(), userDTO.getUsername());
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new EntityExistsException("User with the same username already exists");
        }
        if (userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new EntityExistsException("User with the same phone already exists");
        }
        User user = userMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        return userWithIdMapper.toDTO(user);
    }

    @Transactional
    public void deleteUserById(Long id) {
        log.info("Deleting user by ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user = userOptional.get();
        checkIsDeleted(user.getIsDeleted());
        userRepository.softDeleteByUsername(user.getUsername());
    }
    private void checkIsDeleted(boolean deleted)
    {
        if (deleted)
        {
            throw new IllegalStateException("User has already been deleted");
        }
    }
    private User findActiveUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        User user = userOptional.get();
        checkIsDeleted(user.getIsDeleted());
        return user;
    }

    public UserWithIdDTO getUserProfile(Long id)
    {
        log.info("Retrieving profile for user ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userWithIdMapper.toDTO(user);
    }

    public Page<UserWithIdDTO> getAllProfiles(Pageable pageable) {
        log.info("Retrieving all user profiles, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return userRepository.findAll(pageable).map(userWithIdMapper::toDTO);
    }
    
    @Transactional
    public UserWithoutPasswordDTO updateProfile(Long id,UserDTO userDTO)
    {
        log.info("Updating profile for user ID: {}", id);
        User user= findActiveUserById(id);
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

    private void checkUsername (User user, String username)
    {
        if (userRepository.findByUsername(username).isPresent() &&
                !username.equals(user.getUsername())) {
            throw new EntityExistsException("User with the same username already exists");
        }
    }

    private void checkPhone (User user, String phone)
    {
        if (userRepository.findByPhone(phone).isPresent() &&
                !phone.equals(user.getPhone())) {
            throw new EntityExistsException("User with the same phone already exists");
        }
    }

    private void checkPassword (User user, String password)
    {
        if (passwordEncoder.matches(password, user.getPassword())) {
            throw new SamePasswordException("The new password cannot be the same as the old password.");
        }
    }

    @Transactional
    public UserWithIdDTO changeUser(Long id, UpdateUserDTO updateUserDTO) {
        log.info("Partially updating user with ID: {}", id);

        User user = findActiveUserById(id);

        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().isBlank()) {
            checkUsername(user,updateUserDTO.getUsername());
            user.setUsername(updateUserDTO.getUsername());
        }

        if (updateUserDTO.getPhone() != null && !updateUserDTO.getPhone().isBlank()) {
           checkPhone(user,updateUserDTO.getPhone());
            user.setPhone(updateUserDTO.getPhone());
        }

        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isBlank()) {
            checkPassword(user,updateUserDTO.getPassword());
            user.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        userRepository.save(user);
        return userWithIdMapper.toDTO(user);
    }

    public Page<RideWithIdDTO> getRidesByUserId(Long userId, Integer page, Integer size) {
        log.info("Fetching rides for user ID: {}, page={}, size={}", userId, page, size);

        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return rideServiceClient.getRides(userId, page, size);
    }

    public RideWithIdDTO createRide(Long userId,RideDTO rideDTO)
    {
        log.info("Creating ride for user ID: {}", userId);

        checkDeletedAndExists(userId);
        rideDTO.setUserId(userId);
        return rideServiceClient.createRide(rideDTO);
    }

    public RideWithIdDTO changeRide(Long rideId,Long userId,UpdateRideDTO updateRideDTO)
    {
        log.info("Updating ride with ID: {} for user ID: {}", rideId, userId);

        checkDeletedAndExists(userId);
        updateRideDTO.setUserId(userId);
        return rideServiceClient.changeRide(rideId,updateRideDTO);
    }

    private void checkDeletedAndExists(Long userId)
    {
        User user =  userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("Driver not found"));
        checkIsDeleted(user.getIsDeleted());
    }

    public CarWithIdDTO createCar (CarDTO carCreateDTO)
    {
        log.info("Creating new car with plate: {}", carCreateDTO.getNumber());

        return carServiceClient.createCar(carCreateDTO);
    }

    public CarWithIdDTO changeCar (Long carId,UpdateCarDTO updateCarDTO)
    {
        log.info("Updating car with ID: {}", carId);

        return carServiceClient.changeCar(carId, updateCarDTO);
    }

    public void deleteCar(Long carId)
    {
        log.info("Deleting car with ID: {}", carId);

        carServiceClient.deleteCar(carId);
    }

    public Page<CarWithIdDTO> getAllCars ( Integer page, Integer size)
    {
        log.info("Retrieving all cars, page={}, size={}", page, size);

        return carServiceClient.getAllCars(page,size);
    }

    public PromoCodeWithIdDTO createPromoCode (PromoCodeDTO promoCodeDTO)
    {
        log.info("Creating new promo code: {}", promoCodeDTO.getKeyword());

        return promoCodeServiceClient.createPromoCode(promoCodeDTO);
    }

    public PromoCodeWithIdDTO changePromoCode (Long promoCodeId,UpdatePromoCodeDTO updatePromoCodeDTO)
    {
        log.info("Updating promo code with ID: {}", promoCodeId);

        return promoCodeServiceClient.changePromoCode(promoCodeId, updatePromoCodeDTO);
    }

    public void deletePromoCode(Long promoCodeId)
    {
        log.info("Deleting promo code with ID: {}", promoCodeId);

        promoCodeServiceClient.deletePromoCode(promoCodeId);
    }

    public Page<PromoCodeWithIdDTO> getAllPromoCodes (Pageable pageable)
    {
        log.info("Fetching all promo codes, page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        return promoCodeServiceClient.getAllPromoCodes(pageable);
    }

    public void cancelRide(CanceledRideDTO canceledRideDTO)
    {
        log.info("Cancelling ride ID: {} for user ID: {}", canceledRideDTO.getRideId(), canceledRideDTO.getUserId());

        cancelRideProducer.sendCancelRequest(canceledRideDTO);
    }

    public void checkPromoCode(CheckPromoCodeDTO checkPromoCodeDTO)
    {
        log.info("Checking promo code: {}", checkPromoCodeDTO.getKeyword());

        checkPromoCodeProducer.sendCheckPromoCodeRequest(checkPromoCodeDTO);
    }

    public Page<ClientFeedbackWithIdDTO> getAllFeedbacks (Long userId, Integer page, Integer size)
    {
        log.info("Retrieving all feedbacks for user ID: {}, page={}, size={}", userId, page, size);

        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return feedbackServiceClient.getFeedbacks(userId,page,size);
    }


    public ClientFeedbackWithIdDTO createFeedback(Long userId,ClientFeedbackDTO clientFeedbackDTO)
    {
        log.info("Creating feedback for user ID: {}", userId);

        checkDeletedAndExists(userId);
        clientFeedbackDTO.setUserId(userId);
        return feedbackServiceClient.createClientFeedback(clientFeedbackDTO);
    }

    public RateDTO getUserRate(Long userId)
    {
        log.info("Getting user rating for user ID: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return feedbackServiceClient.getUserRate(userId);
    }

    public ClientFeedbackWithIdDTO changeFeedback (Long feedbackId,UpdateClientRateDTO updateClientRateDTO)
    {
        log.info("Updating feedback with ID: {}", feedbackId);

        return feedbackServiceClient.changeFeedBack(feedbackId, updateClientRateDTO);
    }

    public Page<PaymentWithIdDTO> getAllPaymentsByUser (Long userId, int page,int size)
    {
        log.info("Fetching all payments for user ID: {}, page={}, size={}", userId, page, size);

        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return paymentServiceClient.getAllPaymentsByUser(userId,page,size);
    }

    public PaymentWithIdDTO getPendingPaymentByUser(Long userId)
    {
        log.info("Getting pending payment for user ID: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return paymentServiceClient.getPendingPaymentByUser(userId);
    }

    public PaymentWithIdDTO confirmedPayment(Long userId, Long paymentId, ConfirmedPaymentDTO confirmedPaymentDTO)
    {
        log.info("Confirming payment ID: {} for user ID: {}", paymentId, userId);

        return paymentServiceClient.confirmedPayment(userId,paymentId,confirmedPaymentDTO);
    }
}