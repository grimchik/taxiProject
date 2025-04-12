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

import java.util.Optional;

@Service
public class UserService {
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
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userWithIdMapper.toDTO(user);
    }

    public Page<UserWithIdDTO> getAllProfiles(Pageable pageable) {
        return userRepository.findAll(pageable).map(userWithIdMapper::toDTO);
    }
    
    @Transactional
    public UserWithoutPasswordDTO updateProfile(Long id,UserDTO userDTO)
    {
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
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return rideServiceClient.getRides(userId, page, size);
    }

    public RideWithIdDTO createRide(Long userId,RideDTO rideDTO)
    {
        checkDeletedAndExists(userId);
        rideDTO.setUserId(userId);
        return rideServiceClient.createRide(rideDTO);
    }

    public RideWithIdDTO changeRide(Long rideId,Long userId,UpdateRideDTO updateRideDTO)
    {
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

    public CarWithIdDTO createCar (CarCreateDTO carCreateDTO)
    {
        return carServiceClient.createCar(carCreateDTO);
    }

    public CarWithIdDTO changeCar (Long carId,UpdateCarDTO updateCarDTO)
    {
        return carServiceClient.changeCar(carId, updateCarDTO);
    }

    public void deleteCar(Long carId)
    {
        carServiceClient.deleteCar(carId);
    }

    public Page<CarWithIdDTO> getAllCars ( Integer page, Integer size)
    {
        return carServiceClient.getAllCars(page,size);
    }

    public PromoCodeWithIdDTO createPromoCode (PromoCodeDTO promoCodeDTO)
    {
        return promoCodeServiceClient.createPromoCode(promoCodeDTO);
    }

    public PromoCodeWithIdDTO changePromoCode (Long promoCodeId,UpdatePromoCodeDTO updatePromoCodeDTO)
    {
        return promoCodeServiceClient.changePromoCode(promoCodeId, updatePromoCodeDTO);
    }

    public void deletePromoCode(Long promoCodeId)
    {
        promoCodeServiceClient.deletePromoCode(promoCodeId);
    }

    public Page<PromoCodeWithIdDTO> getAllPromoCodes (Pageable pageable)
    {
        return promoCodeServiceClient.getAllPromoCodes(pageable);
    }

    public void cancelRide(CanceledRideDTO canceledRideDTO)
    {
        cancelRideProducer.sendCancelRequest(canceledRideDTO);
    }

    public void checkPromoCode(CheckPromoCodeDTO checkPromoCodeDTO)
    {
        checkPromoCodeProducer.sendCheckPromoCodeRequest(checkPromoCodeDTO);
    }

    public Page<ClientFeedbackWithIdDTO> getAllFeedbacks (Long userId, Integer page, Integer size)
    {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return feedbackServiceClient.getFeedbacks(userId,page,size);
    }


    public ClientFeedbackWithIdDTO createFeedback(Long userId,ClientFeedbackDTO clientFeedbackDTO)
    {
        checkDeletedAndExists(userId);
        clientFeedbackDTO.setUserId(userId);
        return feedbackServiceClient.createClientFeedback(clientFeedbackDTO);
    }

    public RateDTO getUserRate(Long userId)
    {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return feedbackServiceClient.getUserRate(userId);
    }

    public ClientFeedbackWithIdDTO changeFeedback (Long feedbackId,UpdateClientRateDTO updateClientRateDTO)
    {
        return feedbackServiceClient.changeFeedBack(feedbackId, updateClientRateDTO);
    }

    public Page<PaymentWithIdDTO> getAllPaymentsByUser (Long userId, int page,int size)
    {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return paymentServiceClient.getAllPaymentsByUser(userId,page,size);
    }

    public PaymentWithIdDTO getPendingPaymentByUser(Long userId)
    {
        Optional<User> userOptional = userRepository.findById(userId);
        userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
        return paymentServiceClient.getPendingPaymentByUser(userId);
    }

    public PaymentWithIdDTO confirmedPayment(Long userId, Long paymentId, ConfirmedPaymentDTO confirmedPaymentDTO) {
        return paymentServiceClient.confirmedPayment(userId,paymentId,confirmedPaymentDTO);
    }
}