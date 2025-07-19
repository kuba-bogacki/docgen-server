package com.document.exception;

public class DocxEvidenceReaderException extends RuntimeException {

    public DocxEvidenceReaderException(String message) {
        super(message);
    }

    public DocxEvidenceReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
