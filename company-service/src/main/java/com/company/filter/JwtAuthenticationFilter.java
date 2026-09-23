package com.company.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static com.company.util.ApplicationConstants.USER_EMAIL_HEADER;
import static com.company.util.ApplicationConstants.USER_ROLE_HEADER;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final var userEmail = request.getHeader(USER_EMAIL_HEADER);
        final var userRole = request.getHeader(USER_ROLE_HEADER);

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            final List<GrantedAuthority> userAuthorities = (userRole != null)
                    ? List.of(new SimpleGrantedAuthority("ROLE_" + userRole)) : List.of();

            final var authentication = new UsernamePasswordAuthenticationToken(userEmail, null, userAuthorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
