package com.authentication.service.implementation;

import com.authentication.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Service
@PropertySource(value = "classpath:application.properties")
public class JwtServiceImplementation implements JwtService {

    private final String secretKey;
    private final Integer sessionDuration;

    @Autowired
    public JwtServiceImplementation(
            @Value("${token.secret.key:}") String secretKey,
            @Value("${token.session.duration:}") Integer sessionDuration) {
        this.secretKey = secretKey;
        this.sessionDuration = sessionDuration;
    }

    @Override
    public String extractUsername(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateJwtToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(sessionDuration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateJwtToken(UserDetails userDetails) {
        return generateJwtToken(new HashMap<>(), userDetails);
    }

    @Override
    public boolean isJwtTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername())) && !isJwtTokenExpired(jwtToken);
    }

    @Override
    public boolean isJwtTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    @Override
    public Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    @Override
    public Claims extractAllClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
    }
}