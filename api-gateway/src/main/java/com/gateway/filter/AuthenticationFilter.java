package com.gateway.filter;

import com.gateway.util.JwtUtil;
import com.gateway.util.RouterValidator;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.gateway.util.ApplicationConstants.*;
import static org.springframework.web.cors.reactive.CorsUtils.isCorsRequest;

@RefreshScope
@RequiredArgsConstructor
@Log4j2
@Configuration
public class AuthenticationFilter implements WebFilter {

    private final RouterValidator routerValidator;
    private final JwtUtil jwtUtil;

    @Override
    public @NotNull Mono<Void> filter(ServerWebExchange exchange, @Nullable WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isCorsRequest(request) && !isWebSocketConnection(request)) {
            ServerHttpResponse response = exchange.getResponse();
            HttpHeaders headers = response.getHeaders();

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
            if (this.isAuthMissing(request))
                return this.onError(exchange, "Authorization header is missing in request");

            final String token = this.getValidToken(request);

            if (jwtUtil.isInvalid(token))
                return this.onError(exchange, "Authorization header is invalid");
            this.populateRequestWithHeaders(exchange, token);
        }
        return Objects.requireNonNull(chain).filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.error("Unauthorized error: {}", error);
        return response.setComplete();
    }

    private String getValidToken(ServerHttpRequest request) {
        if (request.getHeaders().containsKey("Cookie")) {
            return request.getHeaders().getOrEmpty("Cookie").get(0).split("=")[1];
        }
        return request.getHeaders().getOrEmpty("Authorization").get(0).split(" ")[1];
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange, String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        exchange.getRequest().mutate()
                .header("id", String.valueOf(claims.get("id")))
                .header("role", String.valueOf(claims.get("role")))
                .build();
    }

    private static boolean isWebSocketConnection(ServerHttpRequest request) {
        return request.getURI().getPath().contains("v1.0/notification/websocket/info");
    }
}
