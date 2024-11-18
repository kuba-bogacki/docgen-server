package com.authentication.config.imagekit;

public interface ImageKitConfiguration {
    String uploadImage(byte[] bytes, String fileName) throws Exception;
    Boolean resultFileListIsEmpty(String fileName) throws Exception;
    void deleteFile(String fileName) throws Exception;
}
