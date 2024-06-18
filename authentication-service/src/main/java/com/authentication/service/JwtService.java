package com.authentication.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String jwtToken);
    <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver);
    String generateJwtToken(Map<String, Object> extraClaims, UserDetails userDetails);
    String generateJwtToken(UserDetails userDetails);
    boolean isJwtTokenValid(String jwtToken, UserDetails userDetails);
    boolean isJwtTokenExpired(String jwtToken);
    Date extractExpiration(String jwtToken);
    Claims extractAllClaims(String jwtToken);
    void validateToken(String token);
}
