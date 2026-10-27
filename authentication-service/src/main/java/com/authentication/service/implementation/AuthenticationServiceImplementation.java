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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
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
    @Transactional
    public void register(RegisterRequest registerRequest) {
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
        final var savedUser = userRepository.save(user);
        log.info("New user with id {} successfully saved.", savedUser.getUserId());
    }

    @Override
    @Transactional
    public void addUserPrincipal(UserPrincipalDto userPrincipalDto) {
        final User user = userRepository.findById(UUID.fromString(userPrincipalDto.getUserId()))
                .orElseThrow(() -> new UserNotFoundException("Impossible to find user with provide id"));

        user.setUserPrincipal(userPrincipalDto.getUserPrincipal());
        final var savedUser = userRepository.save(user);
        log.info("User principal number added and user with id {} successfully updated.", savedUser.getUserId());
    }

    @Override
    @Transactional
    public AuthenticationResponse verifyUserRegistrationCode(String registrationCode, AuthenticationRequest authenticationRequest) {
        final User user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("Impossible to find user with provided email"));

        if (!user.getUserVerificationCode().equals(registrationCode)) {
            throw new UserAuthenticationException("Verification codes are different or code already expired");
        }

        user.setEnabled(true);
        final var savedUser = userRepository.save(user);
        log.info("Enabled account set and user with id {} successfully updated.", savedUser.getUserId());

        authenticateUser(authenticationRequest);
        final String jwtToken = jwtService.generateJwtToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        final User user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("Impossible to find user with provided email"));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new UserAccountDisableException("User account need to be activate first. Check your email for activate link");
        }

        authenticateUser(authenticationRequest);
        final String jwtToken = jwtService.generateJwtToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public void logout(String userEmail) {
        final User user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Impossible to find user by provided email"));

        user.setUserPrincipal(null);
        final var savedUser = userRepository.save(user);
        log.info("User with id {} successfully logout and updated.", savedUser.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public void refreshToken(String userEmail) {
        final User user = userRepository.findUserByUserEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Couldn't find user with provided email"));

        final String userPrincipal = user.getUserPrincipal();
        final String jwtToken = jwtService.generateJwtToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

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
    @Transactional(readOnly = true)
    public AuthenticationResponse confirmCompanyMembership(String companyId, AuthenticationRequest authenticationRequest) {
        final User user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail())
                .orElseThrow(() -> new UserNotFoundException("User with provided email cannot be found"));

        authenticateUser(authenticationRequest);
        final String jwtToken = jwtService.generateJwtToken(user);
        final String refreshToken = jwtService.generateRefreshToken(user);

        final ResponseEntity<?> joiningStatus = httpClient.getJoiningStatus(user, companyId, authenticationRequest.getUserEmail());

        if (!joiningStatus.getStatusCode().is2xxSuccessful()) {
            throw new UserAuthenticationException(String.format("Couldn't add user as a member to company with id: %s.", companyId));
        }

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void checkIfUserExist(final String userEmail) {
        final Optional<User> userFromDatabase = userRepository.findUserByUserEmail(userEmail);
        if (userFromDatabase.isPresent()) {
            throw new UserAlreadyExistException("User " + userEmail + " already exist in database.");
        }
    }

    private void authenticateUser(AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUserEmail(), authenticationRequest.getUserPassword()));
        } catch (Exception e) {
            throw new UserAuthorizationException("Bad credentials. Impossible to authenticate user with provide email or password");
        }
    }
}