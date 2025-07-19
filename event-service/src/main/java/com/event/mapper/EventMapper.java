package com.event.mapper;

import com.event.model.Event;
import com.event.model.dto.EventDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDate.class, LocalTime.class})
public interface EventMapper {

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "dd-MM-yyyy")
    @Mapping(source = "eventTime", target = "eventTime", dateFormat = "HH:mm")
    Event mapToEntity(EventDto eventDto);

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "dd-MM-yyyy")
    @Mapping(source = "eventTime", target = "eventTime", dateFormat = "HH:mm")
    EventDto mapToDto(Event event);

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "dd-MM-yyyy")
    @Mapping(source = "eventTime", target = "eventTime", dateFormat = "HH:mm")
    List<Event> mapToEntities(List<EventDto> eventDtos);

    @Mapping(source = "eventDate", target = "eventDate", dateFormat = "dd-MM-yyyy")
    @Mapping(source = "eventTime", target = "eventTime", dateFormat = "HH:mm")
    List<EventDto> mapToDtos(List<Event> events);
}
