package com.event.model;

import com.event.model.type.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    @Column(nullable = false)
    private UUID eventCompany;

    @Column(nullable = false)
    private String eventTitle;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private LocalTime eventTime;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @ElementCollection
    private Set<UUID> eventMembers = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event other)) return false;
        return this.eventId != null && this.eventId.equals(other.getEventId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
