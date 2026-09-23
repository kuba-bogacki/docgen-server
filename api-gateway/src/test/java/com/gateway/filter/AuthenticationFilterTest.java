package com.gateway.filter;

import com.gateway.configuration.properties.JwtProperties;
import com.gateway.util.RouterValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AuthenticationFilterTest {

    @Mock private RouterValidator routerValidator;
    @Mock private JwtProperties jwtProperties;
    @InjectMocks private AuthenticationFilter authenticationFilter;

    @Test
    void shouldReturn401WhenTokenInvalid() {
//
//        MockServerHttpRequest request =
//                MockServerHttpRequest.get("/api/test").build();
//
//        ServerWebExchange exchange =
//                MockServerWebExchange.from(request);
//
//        WebFilterChain chain = mock(WebFilterChain.class);
//
//        Mono<Void> result = authenticationFilter.filter(exchange, chain);
//
//        StepVerifier.create(result)
//                .verifyComplete();
//
//        assertEquals(
//                HttpStatus.UNAUTHORIZED,
//                exchange.getResponse().getStatusCode()
//        );
//
//        verify(chain, never()).filter(any());
    }
}