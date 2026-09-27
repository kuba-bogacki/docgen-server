package com.authentication.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class PaymentIntentDto {

    private String paymentIntentId;
    private String paymentClientSecret;
}
