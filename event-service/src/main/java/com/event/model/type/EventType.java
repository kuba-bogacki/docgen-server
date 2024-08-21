package com.event.model.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EventType {
    VOTING("Voting"),
    WEEKLY("Weekly"),
    MEETING("Meeting"),
    SUBMIT("Submit");

    private final String eventName;
}
