package com.gateway.filter;

import com.gateway.configuration.properties.JwtProperties;
import com.gateway.util.RouterValidator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.gateway.util.ApplicationConstants.*;

@Slf4j
@Configuration
@RefreshScope
@RequiredArgsConstructor
public class AuthenticationFilter implements WebFilter {

    private final JwtProperties jwtProperties;
    private final RouterValidator routerValidator;

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @Nullable WebFilterChain chain) {
        final var request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (isAuthMissing(request))
                return onError(exchange, "Authorization header is missing in request");

            final var token = getValidToken(request);

            if (jwtProperties.isInvalid(token))
                return onError(exchange, "Authorization header is invalid");
            exchange = populateRequestWithHeaders(exchange, token);
        }
        return Objects.requireNonNull(chain).filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        final var response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.error("Unauthorized error: {}", error);
        return response.setComplete();
    }

    private String getValidToken(ServerHttpRequest request) {
        if (request.getHeaders().containsHeader(AUTHORIZATION_HEADER)) {
            final var authHeaders = request.getHeaders().getOrEmpty(AUTHORIZATION_HEADER);
            if (!authHeaders.isEmpty() && authHeaders.get(0).startsWith(BEARER)) {
                return authHeaders.get(0).substring(BEARER.length()).trim();
            }
        }
        final var authCookie = request.getCookies().getFirst(ACCESS_TOKEN);
        return authCookie != null ? authCookie.getValue() : null;
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        final boolean hasAuthHeader = request.getHeaders().containsHeader(AUTHORIZATION_HEADER);
        final boolean hasAuthCookie = request.getCookies().containsKey(ACCESS_TOKEN);

        return !hasAuthHeader && !hasAuthCookie;
    }

    private ServerWebExchange populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        final var claims = jwtProperties.getAllClaimsFromToken(token);
        final var request = exchange.getRequest().mutate()
                .header(USER_EMAIL_HEADER, String.valueOf(claims.get(SUB)))
                .header(USER_ROLE_HEADER, String.valueOf(claims.get(ROLE)))
                .build();
        return exchange.mutate()
                .request(request)
                .build();
    }
}
