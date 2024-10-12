package com.authentication.util;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class NumberGenerator {

    public String generateVerificationCode() {
        return RandomStringUtils.randomAlphabetic(64);
    }
}
