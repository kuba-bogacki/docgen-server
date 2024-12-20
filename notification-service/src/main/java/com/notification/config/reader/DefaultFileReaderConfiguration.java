package com.notification.config.reader;

import com.notification.exception.ReadEmailContentException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import static com.notification.util.ApplicationConstants.STATIC_FILE_FOLDER;
import static java.nio.charset.StandardCharsets.ISO_8859_1;

@Slf4j
@Component
public class DefaultFileReaderConfiguration implements FileReaderConfiguration {

    @Override
    public String emailFormatterAndReader(String fileName) throws ReadEmailContentException {
        var filePath = STATIC_FILE_FOLDER + fileName;

        try (var reader = new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath)), ISO_8859_1);
             var bufferedReader = new BufferedReader(reader);
        ) {
            String line;
            var emailBody = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                emailBody.append(line);
            }
            return emailBody.toString();
        } catch (Exception e) {
            var message = String.format("Couldn't read email message from %s file", fileName);
            log.error(message, e);
            throw new ReadEmailContentException(message);
        }
    }
}
