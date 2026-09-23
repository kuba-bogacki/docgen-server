package com.notification.configuration.properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.security.Key;

import static com.notification.util.ApplicationConstants.BEARER;
import static com.notification.util.ApplicationConstants.SUB;

@Component
@PropertySource(value = {"classpath:application.properties"})
public class JwtProperties {

    private final String secret;

    @Autowired
    public JwtProperties(@Value("${token.secret.key:}") String secret) {
        this.secret = secret;
    }

    public String getUserEmail(String jwtToken) {
        if (jwtToken != null && jwtToken.startsWith(BEARER)) {
            jwtToken = jwtToken.substring(7);
        }
        final var subClaim = getTokenClaims(jwtToken).get(SUB);
        return subClaim != null ? subClaim.toString() : null;
    }

    private Claims getTokenClaims(String jwtToken) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
