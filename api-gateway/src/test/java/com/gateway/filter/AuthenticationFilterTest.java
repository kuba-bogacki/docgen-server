package com.gateway.filter;

import com.gateway.configuration.properties.JwtProperties;
import com.gateway.util.RouterValidator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class AuthenticationFilterTest {

    @Mock private RouterValidator routerValidator;
    @Mock private JwtProperties jwtProperties;
    @InjectMocks private AuthenticationFilter authenticationFilter;

    @Test
    void shouldReturn401WhenTokenInvalid() {}
}