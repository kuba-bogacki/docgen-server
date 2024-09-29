package com.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints= List.of(
            "/v1.0/authentication/create",
            "/v1.0/authentication/login",
            "/v1.0/authentication/verify/{id}",
            "/v1.0/authentication/reset-password",
            "/v1.0/authentication/confirm-membership/{id}",
            "/v1.0/authentication/send-email-to-reset-password",
            "/v1.0/notification/verification",
            "/v1.0/notification/websocket/{id}"
    );

    private final List<Pattern> openApiPatterns = openApiEndpoints.stream()
            .map(this::convertToPattern)
            .toList();

    private Pattern convertToPattern(String path) {
        String regex = path.replace("{id}", ".*");
        return Pattern.compile(regex);
    }

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiPatterns.stream().noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());

}
