package com.authentication.service.implementation;

import com.authentication.config.ImageKitConfiguration;
import com.authentication.exception.UserAlreadyExistException;
import com.authentication.exception.UserAuthenticationException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.exception.UserPasswordException;
import com.authentication.mapper.UserMapper;
import com.authentication.model.User;
import com.authentication.model.dto.UserDto;
import com.authentication.repository.UserRepository;
import com.authentication.security.AuthenticationRequest;
import com.authentication.service.UserService;
import io.imagekit.sdk.models.FileCreateRequest;
import io.imagekit.sdk.models.GetFileListRequest;
import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultList;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.rmi.NoSuchObjectException;
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
    public Boolean sendVerificationEmail(String userEmail) throws UserNotFoundException, UserPasswordException {
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
            throw new UserPasswordException("Couldn't send reset email. User account still not locked.");
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
        user.get().setUserVerificationCode(RandomString.make(64));
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
    public String uploadNewUserPhoto(MultipartFile multipartFile, String userEmail) throws Exception {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Can't find " + userEmail + " user");
        }

        String userPhotoFileName = String.format("profile-picture-%s.jpg", RandomStringUtils.randomAlphanumeric(26));
        byte[] bytes = multipartFile.getBytes();
        FileCreateRequest fileCreateRequest = new FileCreateRequest(bytes, userPhotoFileName);
        Result result = imageKitConfiguration.imageKitProvider().upload(fileCreateRequest);

        GetFileListRequest getFileListRequest = new GetFileListRequest();
        getFileListRequest.setSearchQuery("name='" + user.get().getUserPhotoFileName() + "'");
        ResultList resultList = imageKitConfiguration.imageKitProvider().getFileList(getFileListRequest);
        if (!resultList.getResults().isEmpty()) {
            imageKitConfiguration.imageKitProvider().deleteFile(resultList.getResults().get(0).getFileId());
        }

        user.get().setUserPhotoFileName(result.getName());
        return userRepository.save(user.get()).getUserPhotoFileName();
    }

    @Override
    public UserDto getUserNotCompanyMember(String companyId, String jwtToken, String userEmail) throws NoSuchObjectException, UserAlreadyExistException {
        Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new NoSuchObjectException("Can't find " + userEmail + " user");
        }

        var formattedCompanyId = formattedCompanyId(companyId);

        List<UUID> membersList = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().get()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/company-members/" + formattedCompanyId))
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

    private String formattedCompanyId(String companyId) {
        return companyId.substring(0, companyId.length() - 1);
    }
}