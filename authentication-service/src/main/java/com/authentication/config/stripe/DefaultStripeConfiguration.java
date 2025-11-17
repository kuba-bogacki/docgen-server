package com.authentication.config.stripe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class DefaultStripeConfiguration implements StripeConfiguration {

    private final String secretKey;

    @Autowired
    public DefaultStripeConfiguration(
            @Value("${stripe.payment.private.key:}") String secretKey) {
        this.secretKey = secretKey;
    }
}
