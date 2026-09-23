package com.gateway.configuration;

import com.gateway.filter.AuthenticationFilter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.util.List;

import static com.gateway.util.ApplicationConstants.*;
import static com.gateway.util.HttpClientUtil.buildUrl;

@Log4j2
@Configuration
@EnableWebFluxSecurity
@PropertySource(value = {"classpath:application.properties"})
public class SecurityConfiguration implements WebFluxConfigurer {

    private final String clientHost;
    private final String clientPort;
    private final AuthenticationFilter authenticationFilter;

    @Autowired
    public SecurityConfiguration(
            @Value("${cors.client.host:}") String clientHost,
            @Value("${cors.client.port:}") String clientPort,
            AuthenticationFilter authenticationFilter) {
        this.clientHost = clientHost;
        this.clientPort = clientPort;
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        final var corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(buildUrl(PROTOCOL, clientHost, clientPort)));
        corsConfig.setAllowedMethods(List.of(ALLOWED_METHODS));
        corsConfig.setAllowedHeaders(List.of(ALLOWED_HEADERS));
        corsConfig.setAllowCredentials(true);

        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
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
                .cors(cors -> {})
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationHandler()))
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return serverHttpSecurity.build();
    }

}