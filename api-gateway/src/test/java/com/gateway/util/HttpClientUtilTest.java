package com.gateway.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpClientUtilTest {

    @Test
    @DisplayName("Should return valid url using provided parameters")
    void test_01() {
        //given
        final String protocol = "https";
        final String clientHost = "localhost";
        final String clientPort = "3000";

        //when
        final String result = HttpClientUtil.buildUrl(protocol, clientHost, clientPort);

        //then
        assertThat(result)
                .isEqualTo("https://localhost:3000");
    }
}