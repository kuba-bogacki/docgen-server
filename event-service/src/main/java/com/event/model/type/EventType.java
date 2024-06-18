package com.event.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {
    VOTING("Voting"),
    MEETING("Meeting"),
    NEGOTIATIONS("Negotiations"),
    PRESENTATION("Presentation"),
    INTEGRATION("Integration");

    private final String eventName;
}
