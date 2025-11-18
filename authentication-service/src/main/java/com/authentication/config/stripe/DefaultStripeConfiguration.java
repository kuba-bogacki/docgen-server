package com.authentication.config.stripe;

import com.authentication.exception.UserPaymentSessionException;
import com.authentication.model.dto.PaymentDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class DefaultStripeConfiguration implements StripeConfiguration {

    private static final int CENTS = 100;

    private final String secretKey;

    @Autowired
    public DefaultStripeConfiguration(@Value("${stripe.payment.private.key:}") String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String createThePaymentIntent(PaymentDto paymentDto) {
        Stripe.apiKey = this.secretKey;

        final var automaticPaymentMethods = PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                .setEnabled(true)
                .build();
        final var paymentIntentCreateParams = PaymentIntentCreateParams.builder()
                .setAmount((long) (paymentDto.getPackagePrice() * CENTS))
                .setCurrency(paymentDto.getPriceCurrency().getDescription())
                .setAutomaticPaymentMethods(automaticPaymentMethods)
                .build();
        try {
            return PaymentIntent.create(paymentIntentCreateParams).getClientSecret();
        } catch (StripeException exception) {
            var message = String.format("Error due creating stripe payment intent: %s", exception.getMessage());
            log.error(message);
            throw new UserPaymentSessionException(message);
        }
    }
}
