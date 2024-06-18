package com.authentication.service.implementation;

import com.authentication.exception.*;
import com.authentication.mapper.UserMapper;
import com.authentication.model.User;
import com.authentication.model.type.Gender;
import com.authentication.model.type.Role;
import com.authentication.repository.UserRepository;
import com.authentication.security.AuthenticationRequest;
import com.authentication.security.AuthenticationResponse;
import com.authentication.security.RegisterRequest;
import com.authentication.service.AuthenticationService;
import com.authentication.service.JwtService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.authentication.util.ApplicationConstants.API_VERSION;
import static com.authentication.util.ApplicationConstants.PROTOCOL;
import static com.authentication.util.UrlBuilder.addTokenHeader;
import static com.authentication.util.UrlBuilder.buildUrl;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImplementation implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final WebClient.Builder webClientBuilder;

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
                .userVerificationCode(RandomString.make(64))
                .termsAndCondition(registerRequest.getTermsAndCondition())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(false)
                .build();
        ResponseEntity<?> emailStatus = webClientBuilder.build().post()
                .uri(buildUrl(PROTOCOL, "notification-service", API_VERSION, "/email/verification"))
                .bodyValue(userMapper.mapToUserEventDto(user))
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block();
        if (Objects.isNull(emailStatus) || emailStatus.getStatusCode().is4xxClientError()) {
            throw new UserAuthenticationException("Couldn't send verification email. New user is not saved in database.");
        }
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponse verifyUserRegistrationCode(String registrationCode, AuthenticationRequest authenticationRequest)
            throws UserNotFoundException, UserAuthenticationException, UserAuthorizationException {
        Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Bad credentials. Impossible to authenticate user with provide email or password.");
        }
        if (!user.get().getUserVerificationCode().equals(registrationCode)) {
            throw new UserAuthenticationException("Verification codes are different or code already expired.");
        }

        user.get().setEnabled(true);
        userRepository.save(user.get());

        authenticateUser(authenticationRequest);
        String jwtToken = jwtService.generateJwtToken(user.get());
        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws UserNotFoundException,
            UserAccountDisableException, UserAuthorizationException {
        Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Bad credentials. Impossible to authenticate user with provide email or password.");
        }
        if (!user.get().getEnabled()) {
            throw new UserAccountDisableException("User account need to be activate first. Check your email for activate link.");
        }

        authenticateUser(authenticationRequest);
        String jwtToken = jwtService.generateJwtToken(user.get());

        Boolean websocketHandshakeSet = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().put()
                .uri(buildUrl(PROTOCOL, "notification-service", API_VERSION, "/notification/set-websocket-handshake/" + user.get().getUserId()))
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();

        if (Objects.nonNull(websocketHandshakeSet) && websocketHandshakeSet.equals(Boolean.FALSE)) {
            throw new UserAccountDisableException("Impossible to set user id websocket handshake.");
        }

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
                .build();
    }

    @Override
    public AuthenticationResponse confirmCompanyMembership(String companyId, AuthenticationRequest authenticationRequest)
            throws UserNotFoundException, UserAuthorizationException {
        Optional<User> user = userRepository.findUserByUserEmail(authenticationRequest.getUserEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("Bad credentials. Impossible to authenticate user with provide email or password.");
        }

        authenticateUser(authenticationRequest);
        String jwtToken = jwtService.generateJwtToken(user.get());

        ResponseEntity<?> joiningStatus = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().put()
                .uri(buildUrl(PROTOCOL, "company-service", API_VERSION, "/company/add-new-member/" + companyId))
                .bodyValue(user.get().getUserId().toString())
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block();

        if (Objects.isNull(joiningStatus) || joiningStatus.getStatusCode().is4xxClientError()) {
            throw new UserAuthenticationException(String.format("Couldn't add user as a member to company with id: %s.", companyId));
        }

        return AuthenticationResponse.builder()
                .jwtToken(jwtToken)
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
        } catch (AuthenticationException e) {
            throw new UserAuthorizationException("Bad credentials. Impossible to authenticate user with provide email or password.");
        }
    }
}