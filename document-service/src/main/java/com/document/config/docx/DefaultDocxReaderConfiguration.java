package com.document.config.docx;

import com.document.exception.DocxEvidenceReaderException;
import com.document.model.dto.DocumentDto;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import static com.document.util.ApplicationConstants.STATIC_FILE_FOLDER;
import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class DefaultDocxReaderConfiguration implements DocxReaderConfiguration {

    @Override
    public DocumentDto generateDocument(Map<String, String> placeholders, String fileName) {
        final var filePath = STATIC_FILE_FOLDER + fileName;
        final var pdfOutputStream = new ByteArrayOutputStream();

        try (var docxReader = requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath))) {
            var wordFile = WordprocessingMLPackage.load(docxReader);
            var mainDocument = wordFile.getMainDocumentPart();
            VariablePrepare.prepare(wordFile);
            mainDocument.variableReplace(placeholders);

            Docx4J.toPDF(wordFile, pdfOutputStream);

            return new DocumentDto(pdfOutputStream.toByteArray());
        } catch (Exception exception) {
            var message = String.format("Error due reading or updating docx file placeholders using provided variables: %s", exception.getMessage());
            log.error(message, exception);
            throw new DocxEvidenceReaderException(message);
        }
    }
}