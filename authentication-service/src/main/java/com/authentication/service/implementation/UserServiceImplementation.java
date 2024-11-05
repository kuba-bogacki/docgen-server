package com.authentication.service.implementation;

import com.authentication.config.imagekit.ImageKitConfiguration;
import com.authentication.exception.*;
import com.authentication.mapper.UserMapper;
import com.authentication.model.User;
import com.authentication.model.dto.UserDto;
import com.authentication.repository.UserRepository;
import com.authentication.security.AuthenticationRequest;
import com.authentication.service.UserService;
import com.authentication.util.NumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.authentication.util.ApplicationConstants.API_VERSION;
import static com.authentication.util.ApplicationConstants.PROTOCOL;
import static com.authentication.util.UrlBuilder.addTokenHeader;
import static com.authentication.util.UrlBuilder.buildUrl;

@Service
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NumberGenerator numberGenerator;
    private final WebClient.Builder webClientBuilder;
    private final ImageKitConfiguration imageKitConfiguration;

    @Override
    public UserDto getUserDtoByUserEmail(String userEmail) {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Can't find " + userEmail + " user");
        }
        return userMapper.mapToUserDto(user.get());
    }

    @Override
    public Boolean sendVerificationEmail(String userEmail) throws UserNotFoundException, UserWebClientException {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to send reset link because user with provide email not exist.");
        }

        ResponseEntity<?> emailStatus = webClientBuilder.build().post()
                .uri(buildUrl(PROTOCOL, "notification-service", API_VERSION, "/notification/reset"))
                .bodyValue(userMapper.mapToUserEventDto(user.get()))
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block();
        if (Objects.isNull(emailStatus) || !emailStatus.getStatusCode().is2xxSuccessful()) {
            throw new UserWebClientException("Couldn't send reset email. User account still locked.");
        }
        user.get().setAccountNonLocked(false);
        User savedUser = userRepository.save(user.get());

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
            throw new UserNotFoundException("Can't find " + userDto.getUserEmail() + " user");
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
            throw new UserNotFoundException("Can't find " + userEmail + " user");
        }
        final var resultFileName = uploadNewMultipartFile(multipartFile, user.get().getUserPhotoFileName());

        user.get().setUserPhotoFileName(resultFileName);
        var savedUser = userRepository.save(user.get());

        return savedUser.getUserPhotoFileName();
    }

    private String uploadNewMultipartFile(MultipartFile multipartFile, String currentUserPhotoFileName) {
        try {
            final var fileName = String.format("profile-picture-%s.jpg", numberGenerator.generateUserPhotoFileName(26));
            final var resultFileName = imageKitConfiguration.uploadImage(multipartFile.getBytes(), fileName);

            if (!imageKitConfiguration.resultFileListIsEmpty(currentUserPhotoFileName)) {
                imageKitConfiguration.deleteFile(currentUserPhotoFileName);
            }
            return resultFileName;
        } catch (Exception exception) {
            throw new UserUploadPhotoException("Couldn't upload result file");
        }
    }

    @Override
    public UserDto getUserNotCompanyMember(String companyId, String jwtToken, String userEmail) throws UserNotFoundException, UserAlreadyExistException {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Can't find " + userEmail + " user");
        }

        List<UUID> membersList = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/company-members/" + companyId.substring(0, companyId.length() - 1)))
                .retrieve()
                .bodyToFlux(UUID.class)
                .collectList()
                .block();

        if (Objects.nonNull(membersList) && membersList.contains(user.get().getUserId())) {
            throw new UserAlreadyExistException("User with email " + userEmail + " is already member of company");
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
}