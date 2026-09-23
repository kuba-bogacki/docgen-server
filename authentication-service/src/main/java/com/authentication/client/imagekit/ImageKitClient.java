package com.authentication.client.imagekit;

public interface ImageKitClient {
    String uploadImage(byte[] bytes, String fileName) throws Exception;
    Boolean resultFileListIsEmpty(String fileName) throws Exception;
    void deleteFile(String fileName) throws Exception;
}
