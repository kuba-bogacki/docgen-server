package com.gateway.configuration;

import com.gateway.configuration.properties.CorsProperties;
import com.gateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebFluxConfigurer {

    private final CorsProperties corsProperties;
    private final AuthenticationFilter authenticationFilter;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final var corsConfig = new CorsConfiguration();

        corsConfig.setAllowedOrigins(List.of(corsProperties.getAllowedOrigin()));
        corsConfig.setAllowedMethods(corsProperties.getAllowedMethods());
        corsConfig.setAllowedHeaders(corsProperties.getAllowedHeaders());
        corsConfig.setAllowCredentials(corsProperties.getAllowedCredentials());
        corsConfig.setMaxAge(corsProperties.getMaxAge());

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(corsProperties.getConfigurationsPath(), corsConfig);

        return source;
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationHandler() {
        return (exchange, exception) -> {
            log.error("Unauthorized error: {}", exception.getMessage());
            return exchange.getResponse().setComplete();
        };
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(CsrfSpec::disable)
                .cors(cors -> corsConfigurationSource())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationHandler()))
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return serverHttpSecurity.build();
    }
}