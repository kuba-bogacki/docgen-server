package com.document.config.docx;

import com.document.model.dto.DocumentDto;

import java.util.Map;

public interface DocxReaderConfiguration {
    DocumentDto generateDocument(Map<String, String> placeholders, String fileName);
}
