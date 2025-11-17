package com.authentication.model.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Membership {

    NONE("NONE"),
    SILVER("SILVER"),
    GOLD("GOLD"),
    DIAMOND("DIAMOND");

    private final String description;

    @JsonCreator
    public static Membership fromString(String description) {
        for (Membership membership : Membership.values()) {
            if (membership.description.equalsIgnoreCase(description)) {
                return membership;
            }
        }
        throw new IllegalArgumentException(String.format("Impossible to create membership type from: %s", description));
    }
}