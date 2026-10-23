package com.notification.configuration.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Getter
@Component
@PropertySource(value = {"classpath:application.properties"})
public class ApplicationClientProperties {

    private final String clientApplicationAddress;

    @Autowired
    public ApplicationClientProperties(@Value("${client.application.address:}") String clientApplicationAddress) {
        this.clientApplicationAddress = clientApplicationAddress;
    }
}
