package com.authentication.service;

import com.authentication.service.implementation.JwtServiceImplementation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.catchThrowable;

class JwtServiceImplementationTest {

    private JwtServiceImplementation jwtServiceImplementation;

    @BeforeEach
    public void setUp() {
        String secretKey = "secretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey";
        Integer sessionDuration = 30;
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
        Assertions.assertThat(result)
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
        Assertions.assertThat(expectedException)
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
        Assertions.assertThat(expectedException)
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
        Assertions.assertThat(result)
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
        Assertions.assertThat(result)
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
        Assertions.assertThat(result)
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
        Assertions.assertThat(result)
                .isNull();
    }
}