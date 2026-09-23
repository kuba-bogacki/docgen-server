package com.authentication.service.implementation;

import com.authentication.service.JwtService;
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

@Service
@PropertySource(value = "classpath:application.properties")
public class JwtServiceImplementation implements JwtService {

    private final String secretKey;
    private final Integer sessionDuration;
    private final Integer refreshExpiration;

    @Autowired
    public JwtServiceImplementation(
            @Value("${token.secret.key:}") String secretKey,
            @Value("${token.session.duration:}") Integer sessionDuration,
            @Value("${token.refresh.expiration:}") Integer refreshExpiration) {
        this.secretKey = secretKey;
        this.sessionDuration = sessionDuration;
        this.refreshExpiration = refreshExpiration;
    }

    @Override
    public String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, Integer expirationInMinutes) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationInMinutes)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String generateJwtToken(UserDetails userDetails) {
        // TODO: Prepare extra claims instead of empty HashMap
        return buildToken(new HashMap<>(), userDetails, sessionDuration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}