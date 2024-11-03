package com.authentication.util;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class NumberGenerator {

    public String generateVerificationCode(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public String generateUserPhotoFileName(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
