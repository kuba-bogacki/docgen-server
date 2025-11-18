package com.authentication.config.stripe;

import com.authentication.model.dto.PaymentDto;

public interface StripeConfiguration {
    String createThePaymentIntent(PaymentDto paymentDto);
}
