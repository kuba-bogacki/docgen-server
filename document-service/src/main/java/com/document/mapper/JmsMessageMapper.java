package com.document.mapper;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class JmsMessageMapper implements MessageConverter {

    private static final String TYPE = "_type";

    private final JsonMapper jsonMapper;

    @Override
    public @NonNull Message toMessage(@NonNull Object object, @NonNull Session session) throws MessageConversionException, JMSException {
        try {
            final String json = jsonMapper.writeValueAsString(object);
            final TextMessage textMessage = session.createTextMessage(json);
            textMessage.setStringProperty(TYPE, object.getClass().getName());
            return textMessage;
        } catch (MessageConversionException | JMSException exception) {
            throw new JMSException(String.format("Failed to convert to JSON. Cause: %s", exception.getMessage()));
        }
    }

    @Override
    public @NonNull Object fromMessage(@NonNull Message message) throws JMSException {
        if (message instanceof TextMessage textMessage) {
            try {
                final String json = textMessage.getText();
                final String className = message.getStringProperty(TYPE);
                final Class<?> clazz = Class.forName(className);
                return jsonMapper.readValue(json, clazz);
            } catch (ClassNotFoundException | JMSException exception) {
                throw new JMSException(String.format("Failed to parse from JSON. Cause: %s", exception.getMessage()));
            }
        }
        throw new JMSException("Only TextMessage is supported.");
    }
}
