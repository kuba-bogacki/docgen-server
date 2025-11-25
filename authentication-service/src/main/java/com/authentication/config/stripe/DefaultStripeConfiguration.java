package com.authentication.config.stripe;

import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.PaymentIntentDto;
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

    @Autowired
    public DefaultStripeConfiguration(@Value("${stripe.payment.private.key:}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    @Override
    public PaymentIntentDto createPaymentIntent(PaymentDto paymentDto) throws StripeException {
        final var automaticPaymentMethods = PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                .setEnabled(true)
                .build();
        final var paymentIntentCreateParams = PaymentIntentCreateParams.builder()
                .setAmount((long) (paymentDto.getPackagePrice() * CENTS))
                .setCurrency(paymentDto.getPriceCurrency().getDescription())
                .setAutomaticPaymentMethods(automaticPaymentMethods)
                .build();
        final var createdPaymentIntent = PaymentIntent.create(paymentIntentCreateParams);
        final var paymentIntentId = createdPaymentIntent.getId();
        final var paymentClientSecret = createdPaymentIntent.getClientSecret();
        return PaymentIntentDto.builder()
                .paymentIntentId(paymentIntentId)
                .paymentClientSecret(paymentClientSecret)
                .build();
    }

    @Override
    public String cancelPaymentIntent(String paymentIntentId) throws StripeException {
        final var paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        final var canceledIntent = paymentIntent.cancel();
        return canceledIntent.getStatus();
    }
}
