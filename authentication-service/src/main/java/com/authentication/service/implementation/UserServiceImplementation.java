package com.authentication.service.implementation;

import com.authentication.client.imagekit.ImageKitClient;
import com.authentication.client.stripe.StripeClient;
import com.authentication.exception.*;
import com.authentication.infrastructure.HttpClient;
import com.authentication.mapper.UserMapper;
import com.authentication.model.User;
import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.PaymentIntentDto;
import com.authentication.model.dto.UserDto;
import com.authentication.model.dto.UserEventDto;
import com.authentication.model.type.Membership;
import com.authentication.repository.UserRepository;
import com.authentication.security.AuthenticationRequest;
import com.authentication.service.UserService;
import com.authentication.util.random.DefaultNumberGenerator;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final DefaultNumberGenerator numberGenerator;
    private final HttpClient httpClient;
    private final ImageKitClient imageKitClient;
    private final StripeClient stripeClient;

    @Override
    public UserDto getUserDtoByUserEmail(String userEmail) {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Can't find %s user", userEmail));
        }
        return userMapper.mapToUserDto(user.get());
    }

    @Override
    public Boolean sendVerificationEmail(String userEmail) throws UserNotFoundException, UserWebClientException {
        final Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to send reset link because user with provide email not exist.");
        }

        final UserEventDto userEventDto = userMapper.mapToUserEventDto(user.get());
        final ResponseEntity<?> emailStatus = httpClient.getEmailStatus(userEventDto);
        if (!emailStatus.getStatusCode().is2xxSuccessful()) {
            throw new UserWebClientException("Couldn't send reset email. User account still locked.");
        }
        user.get().setAccountNonLocked(false);
        final User savedUser = userRepository.save(user.get());

        return savedUser.getAccountNonLocked();
    }

    @Override
    public Boolean resetUserPassword(String verificationCode, AuthenticationRequest authenticationRequest)
        throws UserNotFoundException, UserAuthenticationException {
        Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Bad credentials. Impossible to find user with provide email.");
        }
        if (!user.get().getUserVerificationCode().equals(verificationCode)) {
            throw new UserAuthenticationException("Verification codes are different or code already expired.");
        }

        user.get().setAccountNonLocked(true);
        user.get().setUserVerificationCode(numberGenerator.generateVerificationCode(64));
        user.get().setUserPassword(passwordEncoder.encode(authenticationRequest.getUserPassword()));
        User savedUser = userRepository.save(user.get());

        return savedUser.getAccountNonLocked();
    }

    @Override
    public UserDto updateUserData(UserDto userDto) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUserEmail(userDto.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Can't find %s user", userDto.getUserEmail()));
        }

        user.get().setUserFirstNameI(userDto.getUserFirstNameI());
        user.get().setUserFirstNameII(userDto.getUserFirstNameII());
        user.get().setUserLastNameI(userDto.getUserLastNameI());
        user.get().setUserLastNameII(userDto.getUserLastNameII());
        User savedUser = userRepository.save(user.get());

        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    public String uploadNewUserPhoto(MultipartFile multipartFile, String userEmail) throws UserNotFoundException, UserUploadPhotoException {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Can't find %s user", userEmail));
        }
        final var resultFileName = uploadNewMultipartFile(multipartFile, user.get().getUserPhotoFileName());

        user.get().setUserPhotoFileName(resultFileName);
        var savedUser = userRepository.save(user.get());

        return savedUser.getUserPhotoFileName();
    }

    private String uploadNewMultipartFile(MultipartFile multipartFile, String currentUserPhotoFileName) {
        try {
            final var fileName = String.format("profile-picture-%s.jpg", numberGenerator.generateUserPhotoFileName(26));
            final var resultFileName = imageKitClient.uploadImage(multipartFile.getBytes(), fileName);

            if (!imageKitClient.resultFileListIsEmpty(currentUserPhotoFileName)) {
                imageKitClient.deleteFile(currentUserPhotoFileName);
            }
            return resultFileName;
        } catch (Exception exception) {
            throw new UserUploadPhotoException("Couldn't upload result file");
        }
    }

    @Override
    public UserDto getUserNotCompanyMember(String companyId, String userEmail) throws UserNotFoundException, UserAlreadyExistException {
        final Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Can't find %s user", userEmail));
        }

        final List<UUID> membersList = httpClient.getMemberUuidList(companyId, userEmail);

        if (Objects.nonNull(membersList) && membersList.contains(user.get().getUserId())) {
            throw new UserAlreadyExistException(String.format("User with email %s is already member of company", userEmail));
        }
        return userMapper.mapToUserDto(user.get());
    }

    @Override
    public UserDto getUserDtoByUserId(String userId) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(UUID.fromString(userId));

        if (user.isEmpty()) {
            throw new UserNotFoundException("Can't find user with provided id: " + userId);
        }
        return userMapper.mapToUserDto(user.get());
    }

    @Override
    public PaymentIntentDto createPaymentSession(PaymentDto paymentDto, String userEmail) {
        try {
            final var paymentIntent = stripeClient.createPaymentIntent(paymentDto);
            log.info("Payment intent session with id {} was successfully created.", paymentIntent.getPaymentIntentId());
            return paymentIntent;
        } catch (StripeException exception) {
            log.error("Error due creating stripe payment intent session: {}", exception.getMessage());
            throw new UserPaymentSessionException(exception.getMessage());
        }
    }

    @Override
    public void updateUserMembership(Membership membership, String userEmail) {
        final var user = userRepository.findUserByUserEmail(userEmail);
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("Can't find %s user", userEmail));
        }

        user.get().setUserMembership(membership);
        userRepository.save(user.get());
        log.info("User {} membership was successfully updated.", membership.getDescription());
    }

    @Override
    public String cancelPaymentSession(String paymentIntentId) {
        try {
            final var status = stripeClient.cancelPaymentIntent(paymentIntentId);
            log.info("Payment intent session with id {} was successfully canceled.", paymentIntentId);
            return status;
        } catch (StripeException exception) {
            log.error("Error due cancelling stripe payment intent session: {}", exception.getMessage());
            throw new UserPaymentSessionException(exception.getMessage());
        }
    }
}