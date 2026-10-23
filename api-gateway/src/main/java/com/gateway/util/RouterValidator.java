package com.gateway.util;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.gateway.util.ApplicationConstants.API_VERSION;

@Component
public class RouterValidator {

    public static final List<String> openApiEndpoints= List.of(
            API_VERSION + "/authentication/create",
            API_VERSION + "/authentication/login",
            API_VERSION + "/authentication/refresh",
            API_VERSION + "/authentication/verify/{id}",
            API_VERSION + "/authentication/reset-password/{id}",
            API_VERSION + "/authentication/confirm-membership/{id}",
            API_VERSION + "/authentication/send-email-to-reset-password",
            API_VERSION + "/notification/verification",
            API_VERSION + "/notification/websocket/{id}"
    );

    private final List<Pattern> openApiPatterns = openApiEndpoints.stream()
            .map(this::convertToPattern)
            .toList();

    private Pattern convertToPattern(String path) {
        String regex = path.replace("{id}", ".*");
        return Pattern.compile(regex);
    }

    public Predicate<ServerHttpRequest> isSecured =request -> openApiPatterns.stream()
                    .noneMatch(pattern -> pattern.matcher(request.getURI().getPath()).matches());
}
