package com.gateway.config;

import com.gateway.filter.AuthenticationFilter;
import com.gateway.handler.AuthenticationEntryPointHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.CsrfSpec;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration implements WebFluxConfigurer {

    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final AuthenticationFilter authenticationFilter;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(CsrfSpec::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPointHandler))
                .addFilterAt(authenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        return serverHttpSecurity.build();
    }
}