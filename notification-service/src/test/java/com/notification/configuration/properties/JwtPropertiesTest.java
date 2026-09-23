package com.notification.configuration.properties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

public class JwtPropertiesTest {

    private static final long ONE_HOUR = 1000 * 60 * 60;
    private static final String EMAIL = "user@gmail.com";
    private static final String VALID_SECRET = "VGhpcy1pcy1hLXZlcnktc2VjdXJlLWFuZC1sb25nLXNlY3JldC1rZXktZm9yLXRlc3Rpbmc=";

    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties(VALID_SECRET);
    }

    @Test
    @DisplayName("Should extract email when token has bearer prefix")
    void test_01() {
        //given
        final Date expiration = new Date(System.currentTimeMillis() + ONE_HOUR);
        final String validToken = createTestToken(expiration);
        final String bearerToken = "Bearer " + validToken;

        //when
        final String result = jwtProperties.getUserEmail(bearerToken);

        //then
        assertThat(result)
                .isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Should extract email when token does not have bearer prefix")
    void test_02() {
        //given
        final Date futureExpiration = new Date(System.currentTimeMillis() + ONE_HOUR);
        final String plainToken = createTestToken(futureExpiration);

        //when
        final String result = jwtProperties.getUserEmail(plainToken);

        //then
        assertThat(result)
                .isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("Should throw exception when token is null")
    void test_03() {
        //given
        final String nullToken = null;

        //when
        final Exception expectedException = catchException(() -> jwtProperties.getUserEmail(nullToken));

        //then
        assertThat(expectedException)
                .isInstanceOf(IllegalArgumentException.class);
    }

    private String createTestToken(Date expiration) {
        final byte[] keyBytes = Decoders.BASE64.decode(VALID_SECRET);
        final Key key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject(EMAIL)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }
}