package com.authentication.configuration;

import com.authentication.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static com.authentication.util.ApplicationConstants.API_VERSION;

@Slf4j
@Configuration
@EnableWebMvc
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPointHandler()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(API_VERSION + "/authentication/create").permitAll()
                        .requestMatchers(API_VERSION + "/authentication/login").permitAll()
                        .requestMatchers(API_VERSION + "/authentication/refresh").permitAll()
                        .requestMatchers(API_VERSION + "/authentication/verify/**").permitAll()
                        .requestMatchers(API_VERSION + "/authentication/reset-password/**").permitAll()
                        .requestMatchers(API_VERSION + "/authentication/confirm-membership/**").permitAll()
                        .requestMatchers(API_VERSION + "/authentication/send-email-to-reset-password").permitAll()
                        .anyRequest().authenticated()
                );
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPointHandler() {
        return (request, response, exception) -> {
            log.error("Unauthorized error: {}", exception.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
        };
    }
}
