package com.authentication.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface JwtService {
    String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Integer expiration);
    String generateJwtToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
}
