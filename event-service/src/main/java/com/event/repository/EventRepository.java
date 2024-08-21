package com.event.repository;

import com.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByEventCompany(UUID userId);
    List<Event> findAllByEventCompanyAndEventDate(UUID userId, LocalDate eventDate);
}
