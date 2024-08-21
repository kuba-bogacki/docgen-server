package com.event.model;

import com.event.model.type.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;
    private UUID eventCompany;
    private String eventTitle;
    private LocalDate eventDate;
    private LocalTime eventTime;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    @ElementCollection
    private Set<UUID> eventMembers = new HashSet<>();
}
