package com.authentication.model.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {

    PLN("PLN"),
    USD("USD");

    private final String description;

    @JsonCreator
    public static Currency fromString(String description) {
        for (Currency currency : Currency.values()) {
            if (currency.description.equalsIgnoreCase(description)) {
                return currency;
            }
        }
        throw new IllegalArgumentException(String.format("Impossible to create currency type from: %s", description));
    }
}
