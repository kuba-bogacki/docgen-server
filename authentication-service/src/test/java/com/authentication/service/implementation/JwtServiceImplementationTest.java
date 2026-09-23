package com.authentication.service.implementation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class JwtServiceImplementationTest extends AuthenticationSamples {

    private String secretKey;
    private JwtServiceImplementation jwtServiceImplementation;

    @BeforeEach
    public void setUp() {
        secretKey = "secretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey";
        jwtServiceImplementation = new JwtServiceImplementation(secretKey, tokenSessionDuration, tokenRefreshExpiration);
    }

    @Test
    @DisplayName("Should generate valid jwt token if user details are provided and extra claims are empty")
    void test_01() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.generateJwtToken(sampleUserDetails);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBnbWFpbC5");
        assertThat(extractClaims(result))
                .containsEntry("sub", "john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should generate valid refresh token if user details are provided and extra claims are empty")
    void test_02() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.generateJwtToken(sampleUserDetails);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBnbWFpbC5");
        assertThat(extractClaims(result))
                .containsEntry("sub", "john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should generate valid token if user details are provided and extra claims are provided")
    void test_03() {
        //given
        final Map<String, Object> extraClaims = Map.of("region", "lesser poland");
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.buildToken(extraClaims, sampleUserDetails, tokenSessionDuration);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJyZWdpb24iOiJsZXNzZXIgcG9sYW5kIiwic3ViIjoiam");
        assertThat(extractClaims(result))
                .containsEntry("region", "lesser poland");
    }

    @Test
    @DisplayName("Should throw an exception if user details is null")
    void test_04() {
        //given
        final User nullUserDetails = null;

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.buildToken(Map.of(), nullUserDetails, tokenSessionDuration));

        //then
        Assertions.assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw an exception if extra claims map is null")
    void test_05() {
        //given
        final Map<String, Object> nullExtraClaims = null;
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.buildToken(nullExtraClaims, sampleUserDetails, tokenSessionDuration));

        //then
        Assertions.assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should generate valid token if user details are provided")
    void test_06() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.generateJwtToken(sampleUserDetails);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBnbWFpb");
        assertThat(extractClaims(result))
                .containsEntry("sub", "john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should throw an exception if user details is null when try to generate jwt token")
    void test_07() {
        //given
        final User nullUserDetails = null;

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.generateJwtToken(nullUserDetails));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw an exception if user details is null when try to generate refresh token")
    void test_08() {
        //given
        final User nullUserDetails = null;

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.generateRefreshToken(nullUserDetails));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(NullPointerException.class);
    }

    private Claims extractClaims(String jwtToken) {
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

    private String generateTokenForTestPurposes(long currentTimeMillis, UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(currentTimeMillis))
                .setExpiration(new Date(currentTimeMillis + TimeUnit.MINUTES.toMillis(tokenSessionDuration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}