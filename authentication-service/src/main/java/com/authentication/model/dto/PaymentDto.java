package com.authentication.model.dto;

import com.authentication.model.type.Currency;
import com.authentication.model.type.Membership;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    @NotBlank(message = "Membership can't be null or blank")
    private Membership membership;

    @NotNull(message = "Package price can't be null")
    private Integer packagePrice;

    @NotBlank(message = "Price currency can't be null or blank")
    private Currency priceCurrency;
}
