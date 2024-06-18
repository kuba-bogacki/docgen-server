package com.event.util;

import java.util.Arrays;

public class UrlBuilder {

    public static String buildUrl(String protocol, String applicationName, String version, String... args) {
        StringBuilder url = new StringBuilder();
        url.append(protocol).append("://").append(applicationName).append(version);
        Arrays.stream(args).forEach(url::append);
        return url.toString();
    }
}
