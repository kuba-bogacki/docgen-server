package com.authentication.service.implementation;

import com.authentication.exception.*;
import com.authentication.infrastructure.HttpClient;
import com.authentication.mapper.UserMapper;
import com.authentication.model.User;
import com.authentication.model.dto.UserPrincipalDto;
import com.authentication.model.type.Gender;
import com.authentication.model.type.Membership;
import com.authentication.model.type.Role;
import com.authentication.repository.UserRepository;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.AuthenticationResponse;
import com.authentication.security.RegisterRequest;
import com.authentication.service.AuthenticationService;
import com.authentication.service.JwtService;
import com.authentication.util.random.NumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplementation implements AuthenticationService {

    private final HttpClient httpClient;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NumberGenerator numberGenerator;
    private final AuthenticationManager authenticationManager;

    @Override
    public void register(RegisterRequest registerRequest) throws UserAlreadyExistException, UserAuthenticationException {
        checkIfUserExist(registerRequest.getUserEmail());
        User user = User.builder()
                .userFirstNameI(registerRequest.getUserFirstNameI())
                .userFirstNameII(registerRequest.getUserFirstNameII())
                .userLastNameI(registerRequest.getUserLastNameI())
                .userLastNameII(registerRequest.getUserLastNameII())
                .userEmail(registerRequest.getUserEmail())
                .userPassword(passwordEncoder.encode(registerRequest.getUserPassword()))
                .userGender(Gender.valueOf(registerRequest.getUserGender().toUpperCase()))
                .userRole(Role.USER)
                .userMembership(Membership.NONE)
                .userVerificationCode(numberGenerator.generateVerificationCode(64))
                .termsAndCondition(registerRequest.getTermsAndCondition())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(false)
                .build();
        var userEventDto = userMapper.mapToUserEventDto(user);
        ResponseEntity<?> emailStatus = httpClient.getEmailStatus(userEventDto);
        if (!emailStatus.getStatusCode().is2xxSuccessful()) {
            throw new UserAuthenticationException("Couldn't send verification email. New user is not saved in database.");
        }
        userRepository.save(user);
    }

    @Override
    public void addUserPrincipal(UserPrincipalDto userPrincipalDto) throws UserNotFoundException {
        final Optional<User> user = userRepository.findById(UUID.fromString(userPrincipalDto.getUserId()));

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to find user with provide id");
        }

        user.get().setUserPrincipal(userPrincipalDto.getUserPrincipal());
        userRepository.save(user.get());
    }

    @Override
    public AuthenticationResponse verifyUserRegistrationCode(String registrationCode, AuthenticationRequest authenticationRequest)
            throws UserNotFoundException, UserAuthenticationException, UserAuthorizationException {
        Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to find user with provided email");
        }
        if (!user.get().getUserVerificationCode().equals(registrationCode)) {
            throw new UserAuthenticationException("Verification codes are different or code already expired");
        }

        user.get().setEnabled(true);
        userRepository.save(user.get());

        authenticateUser(authenticationRequest);
        final String jwtToken = jwtService.generateJwtToken(user.get());
        final String refreshToken = jwtService.generateRefreshToken(user.get());
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws UserNotFoundException,
            UserAccountDisableException, UserAuthorizationException {
        Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to find user with provided email");
        }
        if (!user.get().getEnabled()) {
            throw new UserAccountDisableException("User account need to be activate first. Check your email for activate link");
        }

        authenticateUser(authenticationRequest);
        final String jwtToken = jwtService.generateJwtToken(user.get());
        final String refreshToken = jwtService.generateRefreshToken(user.get());
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void refreshToken(String userEmail) {
        final Optional<User> user = userRepository.findUserByUserEmail(userEmail);

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to find user with provided email");
        }

        final String userPrincipal = user.get().getUserPrincipal();
        final String jwtToken = jwtService.generateJwtToken(user.get());
        final String refreshToken = jwtService.generateRefreshToken(user.get());

        final AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        final ResponseEntity<?> refreshTokenStatus =
                httpClient.sendRefreshToken(userPrincipal, authenticationResponse, userEmail);

        if (refreshTokenStatus.getStatusCode().is4xxClientError()) {
            throw new UserAuthenticationException(
                    String.format("Couldn't send refresh token to client with principal name - [%s]", userPrincipal));
        }
    }

    @Override
    public AuthenticationResponse confirmCompanyMembership(String companyId, AuthenticationRequest authenticationRequest)
            throws UserNotFoundException, UserAuthorizationException {
        final Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Impossible to find user with provided email");
        }

        authenticateUser(authenticationRequest);
        final String jwtToken = jwtService.generateJwtToken(user.get());
        final String refreshToken = jwtService.generateRefreshToken(user.get());

        final ResponseEntity<?> joiningStatus = httpClient.getJoiningStatus(user.get(), companyId, authenticationRequest.getUserEmail());

        if (!joiningStatus.getStatusCode().is2xxSuccessful()) {
            throw new UserAuthenticationException(String.format("Couldn't add user as a member to company with id: %s.", companyId));
        }

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void checkIfUserExist(final String userEmail) throws UserAlreadyExistException {
        final Optional<User> userFromDatabase = userRepository.findUserByUserEmail(userEmail);
        if (userFromDatabase.isPresent()) {
            throw new UserAlreadyExistException("User " + userEmail + " already exist in database.");
        }
    }

    private void authenticateUser(AuthenticationRequest authenticationRequest) throws UserAuthorizationException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUserEmail(), authenticationRequest.getUserPassword()));
        } catch (Exception e) {
            throw new UserAuthorizationException("Bad credentials. Impossible to authenticate user with provide email or password");
        }
    }
}