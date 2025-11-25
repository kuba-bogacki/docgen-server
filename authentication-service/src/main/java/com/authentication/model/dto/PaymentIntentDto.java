package com.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentIntentDto {

    private final String paymentIntentId;
    private final String paymentClientSecret;
}
