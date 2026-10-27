package com.event.repository;

import com.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.eventMembers WHERE e.eventCompany = :companyId")
    List<Event> findAllByEventCompany(UUID companyId);

    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN FETCH e.eventMembers WHERE e.eventCompany = :companyId AND e.eventDate = :eventDate")
    List<Event> findAllByEventCompanyAndEventDate(UUID companyId, LocalDate eventDate);
}
