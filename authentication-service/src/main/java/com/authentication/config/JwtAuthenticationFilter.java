package com.authentication.config;

import com.authentication.exception.UserAuthenticationException;
import com.authentication.exception.UserNotFoundException;
import com.authentication.service.JwtService;
import com.authentication.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.authentication.util.ApplicationConstants.*;
import static com.authentication.util.UrlBuilder.addTokenHeader;
import static com.authentication.util.UrlBuilder.buildUrl;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int TIME_LEFT_TO_REFRESH_TOKEN = 5;

    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailsService userDetailsService;
    private final WebClient.Builder webClientBuilder;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwtToken);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isJwtTokenValid(jwtToken, userDetails)) {
                if (refreshTokenShouldBeApplied(minutesLeftToExpireToken(jwtToken))) {
                    final var refreshToken = jwtService.generateJwtToken(userDetails);
                    sendRefreshTokenToClient(jwtToken, refreshToken);
                    response.setHeader("Authorization", "Bearer " + refreshToken);
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
    }

    private long minutesLeftToExpireToken(String jwtToken) {
        final var now = new Date();
        final var expirationDate = jwtService.extractExpiration(jwtToken);

        long diffInMillis = expirationDate.getTime() - now.getTime();
        return diffInMillis / (TimeUnit.MINUTES.toMillis(60));
    }

    private boolean refreshTokenShouldBeApplied(long diffInMinutes) {
        return 0 < diffInMinutes && diffInMinutes < TIME_LEFT_TO_REFRESH_TOKEN;
    }

    private void sendRefreshTokenToClient(String jwtToken, String refreshToken) throws UserNotFoundException {
        final var userPrincipal = userService.getUserDtoByUserEmail(jwtService.extractUsername(jwtToken)).getUserPrincipal();

        ResponseEntity<?> sendRefreshTokenStatus = webClientBuilder
                .filter(addTokenHeader(jwtToken))
                .build().post()
                .uri(buildUrl(PROTOCOL, "notification-service", API_VERSION, "/notification/refresh-token/" + userPrincipal))
                .bodyValue(refreshToken)
                .retrieve()
                .toEntity(ResponseEntity.class)
                .block();

        if (Objects.isNull(sendRefreshTokenStatus) || sendRefreshTokenStatus.getStatusCode().is4xxClientError()) {
            throw new UserAuthenticationException(String.format("Couldn't send refresh token to client with principal name - [%s]", userPrincipal));
        }
    }
}
