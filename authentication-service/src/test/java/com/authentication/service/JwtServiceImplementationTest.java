package com.authentication.service;

import com.authentication.service.implementation.JwtServiceImplementation;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "token.secret.key=test-secret-key",
        "token.session.duration=30"
})
class JwtServiceImplementationTest {

    @MockBean
    private JwtServiceImplementation jwtServiceImplementation;

    @Test
    @DisplayName("Should return jwt token claims if token is valid")
    void test_01() {
        //given
        final var sampleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ikpha3ViIEJvZ2Fja2kiLCJpYXQiOjE1MTYyMzkwMjJ9.WpSriXdwB2F5cRR3xjAeVJcsoHpUF5b1ZIev_glVh1o";

        //when
        final var result = jwtServiceImplementation.extractAllClaims(sampleToken);

        //then
        Assertions.assertThat(result)
                .isNotNull()
                .isInstanceOf(Claims.class);
    }
}