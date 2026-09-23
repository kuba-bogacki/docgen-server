package com.gateway.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpClientUtil {

    public static String buildUrl(String protocol, String clientHost, String clientPort, String... args) {
        final StringBuilder url = new StringBuilder()
                .append(protocol)
                .append("://")
                .append(clientHost)
                .append(":")
                .append(clientPort);
        Arrays.stream(args).forEach(url::append);
        return url.toString();
    }
}
