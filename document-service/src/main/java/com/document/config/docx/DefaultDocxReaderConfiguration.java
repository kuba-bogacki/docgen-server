package com.document.config.docx;

import com.document.exception.DocxEvidenceReaderException;
import com.document.model.dto.DocumentDto;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

import static com.document.util.ApplicationConstants.STATIC_FILE_FOLDER;

@Slf4j
@Component
public class DefaultDocxReaderConfiguration implements DocxReaderConfiguration {

    @Override
    public DocumentDto generateDocument(Map<String, String> placeholders, String fileName) {
        final var docxFile = new File(STATIC_FILE_FOLDER + fileName);
        final var pdfOutputStream = new ByteArrayOutputStream();

        try {
            var wordFile = WordprocessingMLPackage.load(docxFile);
            var mainDocument = wordFile.getMainDocumentPart();
            mainDocument.variableReplace(placeholders);

            Docx4J.toPDF(wordFile, pdfOutputStream);

            return new DocumentDto(pdfOutputStream.toByteArray());
        } catch (Docx4JException | JAXBException exception) {
            var message = String.format("Error due reading or updating docx file placeholders using provided variables: %s", exception.getMessage());
            log.error(message, exception);
            throw new DocxEvidenceReaderException(message);
        }
    }
}
