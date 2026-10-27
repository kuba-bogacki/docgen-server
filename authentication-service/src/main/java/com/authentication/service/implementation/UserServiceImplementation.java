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
import com.authentication.util.random.NumberGenerator;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private static final String USER_NOT_FOUND = "Can't find %s user";

    private final UserMapper userMapper;
    private final HttpClient httpClient;
    private final StripeClient stripeClient;
    private final ImageKitClient imageKitClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NumberGenerator numberGenerator;

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserDtoByUserEmail(String userEmail) {
        final var user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail)));

        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public Boolean sendVerificationEmail(String userEmail) {
        final var user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Impossible to send reset link because user with provide email not exist."));

        final var userEventDto = userMapper.mapToUserEventDto(user);
        final var emailStatus = httpClient.getEmailStatus(userEventDto);
        if (!emailStatus.getStatusCode().is2xxSuccessful()) {
            throw new UserWebClientException("Couldn't send reset email. User account still locked.");
        }
        user.setAccountNonLocked(false);
        final var savedUser = userRepository.save(user);

        log.info("Verification email sent and user with id {} successfully updated.", savedUser.getUserId());
        return savedUser.getAccountNonLocked();
    }

    @Override
    @Transactional
    public Boolean resetUserPassword(String verificationCode, AuthenticationRequest authenticationRequest) {
        final var user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("Bad credentials. Impossible to find user with provide email."));

        if (!user.getUserVerificationCode().equals(verificationCode)) {
            throw new UserAuthenticationException("Verification codes are different or code already expired.");
        }

        user.setAccountNonLocked(true);
        user.setUserVerificationCode(numberGenerator.generateVerificationCode(64));
        user.setUserPassword(passwordEncoder.encode(authenticationRequest.getUserPassword()));
        final var savedUser = userRepository.save(user);

        log.info("Password reset and user with id {} successfully updated.", savedUser.getUserId());
        return savedUser.getAccountNonLocked();
    }

    @Override
    @Transactional
    public UserDto updateUserData(UserDto userDto) {
        final var user = userRepository.findUserByUserEmail(userDto.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, userDto.getUserEmail())));

        user.setUserFirstNameI(userDto.getUserFirstNameI());
        user.setUserFirstNameII(userDto.getUserFirstNameII());
        user.setUserLastNameI(userDto.getUserLastNameI());
        user.setUserLastNameII(userDto.getUserLastNameII());
        final var savedUser = userRepository.save(user);

        log.info("User data with id {} successfully updated.", savedUser.getUserId());
        return userMapper.mapToUserDto(savedUser);
    }

    @Override
    @Transactional
    public String uploadNewUserPhoto(MultipartFile multipartFile, String userEmail) {
        final var user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail)));

        final var resultFileName = uploadNewMultipartFile(multipartFile, user.getUserPhotoFileName());

        user.setUserPhotoFileName(resultFileName);
        final var savedUser = userRepository.save(user);

        log.info("New photo uploaded and user with id {} successfully updated.", savedUser.getUserId());
        return savedUser.getUserPhotoFileName();
    }

    private String uploadNewMultipartFile(MultipartFile multipartFile, String currentUserPhotoFileName) {
        try {
            final var fileName = String.format("profile-picture-%s.jpg", numberGenerator.generateUserPhotoFileName(26));
            final var resultFileName = imageKitClient.uploadImage(multipartFile.getBytes(), fileName);

            if (Boolean.FALSE.equals(imageKitClient.resultFileListIsEmpty(currentUserPhotoFileName))) {
                imageKitClient.deleteFile(currentUserPhotoFileName);
            }
            return resultFileName;
        } catch (Exception exception) {
            throw new UserUploadPhotoException("Couldn't upload result file");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserNotCompanyMember(String companyId, String userEmail) {
        final var user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail)));

        final var membersList = httpClient.getMemberUuidList(companyId, userEmail);

        if (Objects.nonNull(membersList) && membersList.contains(user.getUserId())) {
            throw new UserAlreadyExistException(String.format("User with email %s is already member of company", userEmail));
        }
        return userMapper.mapToUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserDtoByUserId(String userId) {
        final var user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("Can't find user with provided id: " + userId));

        return userMapper.mapToUserDto(user);
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
    @Transactional
    public void updateUserMembership(Membership membership, String userEmail) {
        final var user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(String.format(USER_NOT_FOUND, userEmail)));

        user.setUserMembership(membership);
        final var updatedUser = userRepository.save(user);
        log.info("User {} membership was successfully updated.", updatedUser.getUserMembership().name());
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