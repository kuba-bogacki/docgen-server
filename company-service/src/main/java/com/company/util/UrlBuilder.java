package com.company.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.Arrays;

public class UrlBuilder {

    public static String buildUrl(String protocol, String applicationName, String version, String... args) {
        StringBuilder url = new StringBuilder();
        url.append(protocol).append("://").append(applicationName).append(version);
        Arrays.stream(args).forEach(url::append);
        return url.toString();
    }

    public static ClientHttpRequestInterceptor addTokenHeader(String token) {
        return (clientRequest, body, execution) -> {
            if (!token.isBlank()) {
                HttpHeaders httpHeaders = clientRequest.getHeaders();
                httpHeaders.remove(HttpHeaders.AUTHORIZATION);
                httpHeaders.add(HttpHeaders.AUTHORIZATION, token);
            }
            return execution.execute(clientRequest, body);
        };
    }
}
