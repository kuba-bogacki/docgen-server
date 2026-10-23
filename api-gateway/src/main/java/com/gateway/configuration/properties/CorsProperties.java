package com.gateway.configuration.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
@PropertySource(value = {"classpath:application.properties"})
public class CorsProperties {

    private final Long maxAge;
    private final String allowedOrigin;
    private final String configurationsPath;
    private final Boolean allowedCredentials;
    private final List<String> allowedHeaders;
    private final List<String> allowedMethods;

    @Autowired
    public CorsProperties(
            @Value("${cors.client.max.age:}") Long maxAge,
            @Value("${cors.client.allowed.origin:}") String allowedOrigin,
            @Value("${cors.client.configurations.path:}") String configurationsPath,
            @Value("${cors.client.allowed.credentials:}") Boolean allowedCredentials,
            @Value("${cors.client.allowed.headers:}") List<String> allowedHeaders,
            @Value("${cors.client.allowed.methods:}") List<String> allowedMethods) {
        this.maxAge = maxAge;
        this.allowedOrigin = allowedOrigin;
        this.configurationsPath = configurationsPath;
        this.allowedCredentials = allowedCredentials;
        this.allowedHeaders = allowedHeaders;
        this.allowedMethods = allowedMethods;
    }
}
