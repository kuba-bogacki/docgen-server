package com.gateway.configuration.properties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class JwtPropertiesTest {

    private static final long ONE_HOUR = 1000 * 60 * 60;
    private static final String ROLE = "role";
    private static final String ADMIN = "ADMIN";
    private static final String USERNAME = "joker";
    private static final String VALID_SECRET = "VGhpcy1pcy1hLXZlcnktc2VjdXJlLWFuZC1sb25nLXNlY3JldC1rZXktZm9yLXRlc3Rpbmc=";
    private static final String INVALID_SECRET = "QW5vdGhlci12ZXJ5LXNlY3VyZS1hbmQtbG9uZy1zZWNyZXQta2V5LWZvci10ZXN0aW5n";

    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(VALID_SECRET);
    }

    @Test
    @DisplayName("Should extract all claims from valid token")
    void test_01() {
        //given
        final Date expiration = new Date(System.currentTimeMillis() + ONE_HOUR);
        final String validToken = createTestToken(expiration, VALID_SECRET);

        //when
        final Claims result = jwtProperties.getAllClaimsFromToken(validToken);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getSubject()).isEqualTo(USERNAME);
        assertThat(result.get(ROLE)).isEqualTo(ADMIN);
    }

    @Test
    @DisplayName("Should throw exception when token is expired")
    void test_02() {
        // given
        final Date pastExpiration = new Date(System.currentTimeMillis() - ONE_HOUR);
        final String expiredToken = createTestToken(pastExpiration, VALID_SECRET);

        //when
        final Exception expectedException = catchException((() -> jwtProperties.getAllClaimsFromToken(expiredToken)));

        //then
        assertThat(expectedException)
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    @DisplayName("Should throw exception when token has invalid signature")
    void test_03() {
        // given
        final Date pastExpiration = new Date(System.currentTimeMillis() + ONE_HOUR);
        final String forgedToken = createTestToken(pastExpiration, INVALID_SECRET);

        //when
        final Exception expectedException = catchException((() -> jwtProperties.getAllClaimsFromToken(forgedToken)));

        //then
        assertThat(expectedException)
                .isInstanceOf(SignatureException.class)
                .hasMessageContaining("JWT signature does not match");
    }

    @Test
    @DisplayName("Should throw exception when token is malformed")
    void test_04() {
        // given
        String garbageString = "garbage.token.jwt";

        //when
        final Exception expectedException = catchException((() -> jwtProperties.getAllClaimsFromToken(garbageString)));

        //then
        assertThat(expectedException)
                .isInstanceOf(MalformedJwtException.class);
    }

    @Test
    @DisplayName("Should return false when token is valid and not expired")
    void test_05() {
        //given
        final Date expiration = new Date(System.currentTimeMillis() + ONE_HOUR);
        final String validToken = createTestToken(expiration, VALID_SECRET);

        //when
        final boolean isInvalid = jwtProperties.isInvalid(validToken);

        //then
        assertThat(isInvalid)
                .isFalse();
    }

    @Test
    @DisplayName("Should return true when token is invalid and expired")
    void test_06() {
        // given
        final Date pastExpiration = new Date(System.currentTimeMillis() - ONE_HOUR);
        final String expiredToken = createTestToken(pastExpiration, VALID_SECRET);

        //when
        final boolean isInvalid = jwtProperties.isInvalid(expiredToken);

        //then
        assertThat(isInvalid)
                .isTrue();
    }

    private String createTestToken(Date expiration, String base64Secret) {
        final byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        final Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(USERNAME)
                .claim(ROLE, ADMIN)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
}