package com.notification.util;

import org.apache.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.Arrays;

public class UrlBuilder {

    public static String buildUrl(String protocol, String host, String version, String... args) {
        StringBuilder url = new StringBuilder();
        url.append(protocol).append("://").append(host).append(version);
        Arrays.stream(args).forEach(url::append);
        return url.toString();
    }

    public static ExchangeFilterFunction addTokenHeader(String token) {
        return (clientRequest, next) -> {
            if (!token.isBlank()) {
                ClientRequest.Builder requestBuilder = ClientRequest.from(clientRequest);
                requestBuilder.headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION));
                return next.exchange(requestBuilder
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .build());
            }
            return next.exchange(clientRequest);
        };
    }
}
