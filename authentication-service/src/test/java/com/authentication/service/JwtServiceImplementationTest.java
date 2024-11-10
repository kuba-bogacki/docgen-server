package com.authentication.service;

import com.authentication.service.implementation.JwtServiceImplementation;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class JwtServiceImplementationTest extends AuthenticationSamples {

    private final static SimpleDateFormat LOCAL_DATE_FORMATTER = new SimpleDateFormat("HH:mm:ss");


    private String secretKey;
    private Integer sessionDuration;
    private JwtServiceImplementation jwtServiceImplementation;

    @BeforeEach
    public void setUp() {
        sessionDuration = 10;
        secretKey = "secretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey";
        jwtServiceImplementation = new JwtServiceImplementation(secretKey, sessionDuration);
    }

    @Test
    @DisplayName("Should return jwt token claims if token is valid")
    void test_01() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik1pY2hhZWwgSm9yZGFuIiwiZW1haWwiOiJtaWNoYWVsLmpvcmRhbkBpbnRlcmlhLnBsIn0.qEtLrt7IVA1levwqN0JX5BV6V_qndXwedurTF93xKV8";

        //when
        final var result = jwtServiceImplementation.extractAllClaims(sampleToken);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(Claims.class)
                .containsEntry("name", "Michael Jordan")
                .containsEntry("email", "michael.jordan@interia.pl");
    }

    @Test
    @DisplayName("Should throw an exception if token is valid but signature have encoded different secret key")
    void test_02() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik1pY2hhZWwgSm9yZGFuIiwiZW1haWwiOiJtaWNoYWVsLmpvcmRhbkBpbnRlcmlhLnBsIn0.36pATs5ycGcdQgUdd-yKHkF7wvkGp3CAWmkUGjxtztA";

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.extractAllClaims(sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(SignatureException.class)
                .hasMessageContaining("JWT signature does not match locally computed signature");
    }

    @Test
    @DisplayName("Should throw an exception if token is malformed")
    void test_03() {
        //given
        final var sampleToken = "wrong-token-form";

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.extractAllClaims(sampleToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(MalformedJwtException.class)
                .hasMessageContaining("JWT strings must contain exactly 2 period characters");
    }

    @Test
    @DisplayName("Should return extracted username if token is valid")
    void test_04() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJNaWNoYWVsIEpvcmRhbiIsImVtYWlsIjoibWljaGFlbC5qb3JkYW5AaW50ZXJpYS5wbCJ9.xcxcwIPx_ku0iOpLM8Fi9FZFNlPUVa3s9u-5yLT_9pI";

        //when
        final var result = jwtServiceImplementation.extractUsername(sampleToken);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo("Michael Jordan");
    }

    @Test
    @DisplayName("Should return issuer if token is valid and claims resolver get issuer")
    void test_05() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJNaWNoYWVsIEpvcmRhbiIsImVtYWlsIjoibWljaGFlbC5qb3JkYW5AaW50ZXJpYS5wbCIsImlzcyI6IlNwcmluZyBCb290In0.n3fFimEf5DhhvxPTjASZwFcQGfRWh8eSYHHZuW_FcAc";

        //when
        final var result = jwtServiceImplementation.extractClaim(sampleToken, Claims::getIssuer);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo("Spring Boot");
    }

    @Test
    @DisplayName("Should return id if token is valid and claims resolver get id")
    void test_06() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJNaWNoYWVsIEpvcmRhbiIsImVtYWlsIjoibWljaGFlbC5qb3JkYW5AaW50ZXJpYS5wbCIsImp0aSI6IjAxMjM0NTY3ODkifQ.rqQqyAUZnJO16oy-0zFzeVLL3dVAtKXUzYeEMQdPYgA";

        //when
        final var result = jwtServiceImplementation.extractClaim(sampleToken, Claims::getId);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .isEqualTo("0123456789");
    }

    @Test
    @DisplayName("Should return null if token not contain correct claim")
    void test_07() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJNaWNoYWVsIEpvcmRhbiIsImVtYWlsIjoibWljaGFlbC5qb3JkYW5AaW50ZXJpYS5wbCIsImlzcyI6IlNwcmluZyBCb290In0.n3fFimEf5DhhvxPTjASZwFcQGfRWh8eSYHHZuW_FcAc";

        //when
        final var result = jwtServiceImplementation.extractClaim(sampleToken, Claims::getId);

        //then
        assertThat(result)
                .isNull();
    }

    @Test
    @DisplayName("Should generate valid token if user details are provided and extra claims are empty")
    void test_08() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.generateJwtToken(Collections.emptyMap(), sampleUserDetails);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBnbWFpbC5jb20iLCJpYXQiOjE3MzEyN");
        assertThat(extractClaims(result))
                .containsEntry("sub", "john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should generate valid token if user details are provided and extra claims are provided")
    void test_09() {
        //given
        final Map<String, Object> extraClaims = Map.of("region", "lesser poland");
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.generateJwtToken(extraClaims, sampleUserDetails);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJyZWdpb24iOiJsZXNzZXIgcG9sYW5kIiwic3ViIjoiam9obi5kb2VAZ21haWwuY29tIiwiaWF0IjoxNzMxM");
        assertThat(extractClaims(result))
                .containsEntry("region", "lesser poland");
    }

    @Test
    @DisplayName("Should throw an exception if user details is null")
    void test_10() {
        //given
        final User nullUserDetails = null;

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.generateJwtToken(Map.of(), nullUserDetails));

        //then
        Assertions.assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw an exception if extra claims map is null")
    void test_11() {
        //given
        final Map<String, Object> nullExtraClaims = null;
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.generateJwtToken(nullExtraClaims, sampleUserDetails));

        //then
        Assertions.assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should generate valid token if user details are provided")
    void test_12() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());

        //when
        final var result = jwtServiceImplementation.generateJwtToken(sampleUserDetails);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(String.class)
                .contains("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huLmRvZUBnbWFpbC5jb20iLCJpYXQiOjE3MzEyN");
        assertThat(extractClaims(result))
                .containsEntry("sub", "john.doe@gmail.com");
    }

    @Test
    @DisplayName("Should throw an exception if user details is null")
    void test_13() {
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
    @DisplayName("Should return true if provided token is valid user details are provided")
    void test_14() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(System.currentTimeMillis(), sampleUserDetails);

        //when
        final var result = jwtServiceImplementation.isJwtTokenValid(sampleJwtToken, sampleUserDetails);

        //then
        assertThat(result)
                .isTrue();
    }

    @Test
    @DisplayName("Should return false if provided user details are different")
    void test_15() {
        //given
        final var differentUserEmail = "michael.jackson@gmail.com";
        final var differentUserPassword = "wrong-password";
        final var userDetailsDifferentUserEmail = new User(differentUserEmail, differentUserPassword, Collections.emptyList());
        final var tokenCreationUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(System.currentTimeMillis(), tokenCreationUserDetails);

        //when
        final var result = jwtServiceImplementation.isJwtTokenValid(sampleJwtToken, userDetailsDifferentUserEmail);

        //then
        assertThat(result)
                .isFalse();
    }

    @Test
    @DisplayName("Should throw an exception if provided token is expired")
    void test_16() {
        //given
        final var hourBeforeCurrentTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);
        final var tokenCreationUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(hourBeforeCurrentTime, tokenCreationUserDetails);

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.isJwtTokenValid(sampleJwtToken, tokenCreationUserDetails));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    @DisplayName("Should return false if provided token is not expired")
    void test_17() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(System.currentTimeMillis(), sampleUserDetails);

        //when
        final var result = jwtServiceImplementation.isJwtTokenExpired(sampleJwtToken);

        //then
        assertThat(result)
                .isFalse();
    }

    @Test
    @DisplayName("Should throw an exception if provided token is expired")
    void test_18() {
        //given
        final var hourBeforeCurrentTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);
        final var tokenCreationUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(hourBeforeCurrentTime, tokenCreationUserDetails);

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.isJwtTokenExpired(sampleJwtToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    @DisplayName("Should return expiration date if provided token is valid")
    void test_19() {
        //given
        final long currentTimeInMillis = System.currentTimeMillis();
        final var expectedExpirationDate = new Date(currentTimeInMillis + TimeUnit.MINUTES.toMillis(sessionDuration));
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(currentTimeInMillis, sampleUserDetails);

        //when
        final var result = jwtServiceImplementation.extractExpiration(sampleJwtToken);

        //then
        assertThat(result)
                .isNotNull()
                .isInstanceOf(Date.class);
        assertThat(LOCAL_DATE_FORMATTER.format(result))
                .isEqualTo(LOCAL_DATE_FORMATTER.format(expectedExpirationDate));
    }

    @Test
    @DisplayName("Should throw an exception if couldn't extract date from expired token")
    void test_20() {
        //given
        final var hourBeforeCurrentTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);
        final var tokenCreationUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(hourBeforeCurrentTime, tokenCreationUserDetails);

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.extractExpiration(sampleJwtToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
    }

    @Test
    @DisplayName("Should pass if token validation succeeded")
    void test_21() {
        //given
        final var sampleUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(System.currentTimeMillis(), sampleUserDetails);

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.validateToken(sampleJwtToken));

        //then
        assertThat(expectedException)
                .isNull();
    }

    @Test
    @DisplayName("Should throw an exception if token validation failed")
    void test_22() {
        //given
        final var hourBeforeCurrentTime = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(1);
        final var tokenCreationUserDetails = new User(userEmail, userEncodedPassword, Collections.emptyList());
        final var sampleJwtToken = generateTokenForTestPurposes(hourBeforeCurrentTime, tokenCreationUserDetails);

        //when
        final var expectedException = catchThrowable(() -> jwtServiceImplementation.validateToken(sampleJwtToken));

        //then
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(ExpiredJwtException.class)
                .hasMessageContaining("JWT expired");
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
                .setExpiration(new Date(currentTimeMillis + TimeUnit.MINUTES.toMillis(sessionDuration)))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}