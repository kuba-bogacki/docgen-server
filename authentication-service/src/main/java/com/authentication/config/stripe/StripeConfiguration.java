package com.authentication.config.stripe;

import com.authentication.model.dto.PaymentDto;
import com.authentication.model.dto.PaymentIntentDto;
import com.stripe.exception.StripeException;

public interface StripeConfiguration {
    PaymentIntentDto createPaymentIntent(PaymentDto paymentDto) throws StripeException;
    String cancelPaymentIntent(String paymentIntentId) throws StripeException;
}
