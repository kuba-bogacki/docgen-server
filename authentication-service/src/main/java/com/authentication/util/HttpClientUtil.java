package com.authentication.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.Arrays;

import static com.authentication.util.ApplicationConstants.USER_EMAIL_HEADER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HttpClientUtil {

    public static String buildUrl(String protocol, String applicationName, String version, String... args) {
        final var url = new StringBuilder()
                .append(protocol)
                .append("://")
                .append(applicationName)
                .append(version);
        Arrays.stream(args).forEach(url::append);
        return url.toString();
    }

    public static ClientHttpRequestInterceptor setRequestAttributes(String userEmail) {
        return (request, body, execution) -> {
            if (StringUtils.isNotBlank(userEmail)) {
                request.getHeaders().remove(USER_EMAIL_HEADER);
                request.getHeaders().add(USER_EMAIL_HEADER, userEmail);
            }
            return execution.execute(request, body);
        };
    }
}
