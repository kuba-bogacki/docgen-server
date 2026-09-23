package com.authentication.util;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;

import static com.authentication.util.ApplicationConstants.USER_EMAIL_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpClientUtilTest {

    private MockClientHttpRequest clientHttpRequest;
    @Mock private ClientHttpRequestExecution clientHttpRequestExecution;
    @Mock private ClientHttpResponse clientHttpResponse;

    @BeforeEach
    void setUp() {
        clientHttpRequest = new MockClientHttpRequest();
    }

    @Test
    @DisplayName("Should return valid url using provided parameters")
    void test_01() {
        //given
        final String protocol = "https";
        final String clientHost = "8.8.8.8";
        final String apiVersion = "/v1.0";

        //when
        final String result = HttpClientUtil.buildUrl(protocol, clientHost, apiVersion);

        //then
        assertThat(result)
                .isEqualTo("https://8.8.8.8/v1.0");
    }

    @Test
    @SneakyThrows
    @DisplayName("Should return updated request header when user email is not blank")
    void test_02() {
        //given
        final String oldEmail = "old_user@gmail.com";
        final String newEmail = "new_user@gmail.com";
        final byte[] body = new byte[0];
        clientHttpRequest.getHeaders().add(USER_EMAIL_HEADER, oldEmail);

        //when
        when(clientHttpRequestExecution.execute(clientHttpRequest, body)).thenReturn(clientHttpResponse);

        final ClientHttpRequestInterceptor result = HttpClientUtil.setRequestAttributes(newEmail);
        result.intercept(clientHttpRequest, body, clientHttpRequestExecution);

        //then
        assertThat(clientHttpRequest.getHeaders().get(USER_EMAIL_HEADER))
                .containsExactly(newEmail);
        verify(clientHttpRequestExecution, times(1))
                .execute(clientHttpRequest, body);
    }

    @Test
    @SneakyThrows
    @DisplayName("Should not modify headers when email is null")
    void test_03() {
        //given
        final String oldEmail = "old_user@gmail.com";
        final byte[] body = new byte[0];
        clientHttpRequest.getHeaders().add(USER_EMAIL_HEADER, oldEmail);

        //when
        when(clientHttpRequestExecution.execute(clientHttpRequest, body)).thenReturn(clientHttpResponse);

        final ClientHttpRequestInterceptor result = HttpClientUtil.setRequestAttributes(null);
        result.intercept(clientHttpRequest, body, clientHttpRequestExecution);

        //then
        assertThat(clientHttpRequest.getHeaders().get(USER_EMAIL_HEADER))
                .containsExactly(oldEmail);
        verify(clientHttpRequestExecution, times(1))
                .execute(clientHttpRequest, body);
    }
}