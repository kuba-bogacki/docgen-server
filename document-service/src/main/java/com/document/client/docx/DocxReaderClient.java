package com.document.client.docx;

import com.document.model.dto.DocumentDto;

import java.util.Map;

public interface DocxReaderClient {
    DocumentDto generateDocument(Map<String, String> placeholders, String fileName);
}
