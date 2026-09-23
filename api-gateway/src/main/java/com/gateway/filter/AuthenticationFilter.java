package com.gateway.filter;

import com.gateway.configuration.properties.JwtProperties;
import com.gateway.util.RouterValidator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.cors.reactive.CorsUtils;
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

    private final RouterValidator routerValidator;
    private final JwtProperties jwtProperties;

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @Nullable WebFilterChain chain) {
        final var request = exchange.getRequest();

        if (CorsUtils.isCorsRequest(request)) {
            final var response = exchange.getResponse();
            final var headers = response.getHeaders();

            if (Objects.isNull(headers.get(ACCESS_CONTROL_ALLOW_HEADERS))) {
                headers.add(ACCESS_CONTROL_ALLOW_HEADERS, ALLOWED_HEADERS);
            }
            if (Objects.isNull(headers.get(ACCESS_CONTROL_ALLOW_ORIGIN))) {
                headers.add(ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN);
            }
            if (Objects.isNull(headers.get(ACCESS_CONTROL_ALLOW_METHODS))) {
                headers.add(ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
            }
            if (Objects.isNull(headers.get(ACCESS_CONTROL_ALLOW_CREDENTIALS))) {
                headers.add(ACCESS_CONTROL_ALLOW_CREDENTIALS, ALLOWED_CREDENTIALS);
            }

            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }

        if (routerValidator.isSecured.test(request)) {
            if (isAuthMissing(request))
                return onError(exchange, "Authorization header is missing in request");

            final var token = getValidToken(request);

            if (jwtProperties.isInvalid(token))
                return onError(exchange, "Authorization header is invalid");
            populateRequestWithHeaders(exchange, token);
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
        if (request.getHeaders().containsKey(COOKIE)) {
            return request.getHeaders().getOrEmpty(COOKIE).get(0).split("=")[1];
        }
        return request.getHeaders().getOrEmpty(AUTHORIZATION_HEADER).get(0).split(" ")[1];
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey(AUTHORIZATION_HEADER);
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        final var claims = jwtProperties.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header(USER_EMAIL_HEADER, String.valueOf(claims.get(SUB)))
                .header(USER_ROLE_HEADER, String.valueOf(claims.get(ROLE)))
                .build();
    }
}
