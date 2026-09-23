package com.authentication.util.random;

public interface NumberGenerator {
    String generateVerificationCode(int length);
    String generateUserPhotoFileName(int length);
}
