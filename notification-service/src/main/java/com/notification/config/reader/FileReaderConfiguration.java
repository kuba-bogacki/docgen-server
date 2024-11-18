package com.notification.config.reader;

import com.notification.exception.ReadEmailContentException;

public interface FileReaderConfiguration {
    String emailFormatterAndReader(String fileName) throws ReadEmailContentException;
}
