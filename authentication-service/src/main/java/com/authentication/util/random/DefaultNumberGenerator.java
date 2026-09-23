package com.authentication.util.random;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class DefaultNumberGenerator implements NumberGenerator {

    @Override
    public String generateVerificationCode(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    @Override
    public String generateUserPhotoFileName(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
