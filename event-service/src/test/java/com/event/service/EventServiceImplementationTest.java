package com.event.service;

import com.event.mapper.EventMapper;
import com.event.model.dto.EventDto;
import com.event.repository.EventRepository;
import com.event.service.implementation.EventServiceImplementation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplementationTest extends EventSamples {

    @Mock private EventRepository eventRepository;
    @Mock private EventMapper eventMapper;
    @InjectMocks private EventServiceImplementation eventService;

    @Test
    @DisplayName("Should return natural order sorted event dto list if any event was found and company id and date are valid")
    void test_01() {
        //given
        final var sampleCompanyId = "2b93636c-a257-4e95-bbe0-a0ed4c3c4072";
        final var sampleEventDateString = "12-12-2023";

        //when
        when(eventRepository.findAllByEventCompanyAndEventDate(eventCompanyI, eventDate)).thenReturn(List.of(sampleEventEntityI, sampleEventEntityII));
        when(eventMapper.mapToDtos(List.of(sampleEventEntityII, sampleEventEntityI))).thenReturn(List.of(sampleEventDtoII, sampleEventDtoI));

        final var result = eventService.getCompanyEventsByDate(sampleCompanyId, sampleEventDateString);

        //then
        verify(eventRepository).findAllByEventCompanyAndEventDate(eventCompanyI, eventDate);
        verify(eventMapper).mapToDtos(List.of(sampleEventEntityII, sampleEventEntityI));
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(2)
                .isSortedAccordingTo(Comparator.comparing(EventDto::getEventTime));
    }

    @Test
    @DisplayName("Should return empty list if no event was found and company id and date are valid")
    void test_02() {
        //given
        final var sampleCompanyId = "2b93636c-a257-4e95-bbe0-a0ed4c3c4072";
        final var sampleEventDateString = "12-12-2023";

        //when
        when(eventRepository.findAllByEventCompanyAndEventDate(eventCompanyI, eventDate)).thenReturn(Collections.emptyList());
        when(eventMapper.mapToDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        final var result = eventService.getCompanyEventsByDate(sampleCompanyId, sampleEventDateString);

        //then
        verify(eventRepository).findAllByEventCompanyAndEventDate(eventCompanyI, eventDate);
        verify(eventMapper).mapToDtos(Collections.emptyList());
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .isEmpty();
    }

    @Test
    @DisplayName("Should throw an exception if string date is invalid")
    void test_03() {
        //given
        final var sampleCompanyId = "2b93636c-a257-4e95-bbe0-a0ed4c3c4072";
        final var malformedEventDateString = "wrong-date_toParse";

        //when
        final var expectedException = catchException(() -> eventService.getCompanyEventsByDate(sampleCompanyId, malformedEventDateString));

        //then
        verify(eventRepository, never()).findAllByEventCompanyAndEventDate(any(), any());
        verify(eventMapper, never()).mapToDtos(anyList());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(DateTimeParseException.class);
    }

    @Test
    @DisplayName("Should throw an exception if string company id is invalid")
    void test_04() {
        //given
        final var malformedCompanyId = "wrong-company-id";
        final var sampleEventDateString = "12-12-2023";

        //when
        final var expectedException = catchException(() -> eventService.getCompanyEventsByDate(malformedCompanyId, sampleEventDateString));

        //then
        verify(eventRepository, never()).findAllByEventCompanyAndEventDate(any(), any());
        verify(eventMapper, never()).mapToDtos(anyList());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should return event dto list if any event was found and company id is valid")
    void test_05() {
        //given
        final var sampleCompanyId = "2b93636c-a257-4e95-bbe0-a0ed4c3c4072";

        //when
        when(eventRepository.findAllByEventCompany(eventCompanyI)).thenReturn(List.of(sampleEventEntityI, sampleEventEntityII));
        when(eventMapper.mapToDtos(List.of(sampleEventEntityI, sampleEventEntityII))).thenReturn(List.of(sampleEventDtoI, sampleEventDtoII));

        final var result = eventService.getCompanyEvents(sampleCompanyId);

        //then
        verify(eventRepository).findAllByEventCompany(eventCompanyI);
        verify(eventMapper).mapToDtos(List.of(sampleEventEntityI, sampleEventEntityII));
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Should return empty list if no event was found and company id and date are valid")
    void test_06() {
        //given
        final var sampleCompanyId = "2b93636c-a257-4e95-bbe0-a0ed4c3c4072";

        //when
        when(eventRepository.findAllByEventCompany(eventCompanyI)).thenReturn(Collections.emptyList());
        when(eventMapper.mapToDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        final var result = eventService.getCompanyEvents(sampleCompanyId);

        //then
        verify(eventRepository).findAllByEventCompany(eventCompanyI);
        verify(eventMapper).mapToDtos(Collections.emptyList());
        assertThat(result)
                .isNotNull()
                .isInstanceOf(List.class)
                .isEmpty();
    }

    @Test
    @DisplayName("Should throw an exception if string company id is invalid")
    void test_07() {
        //given
        final var malformedCompanyId = "wrong-company-id";

        //when
        final var expectedException = catchException(() -> eventService.getCompanyEvents(malformedCompanyId));

        //then
        verify(eventRepository, never()).findAllByEventCompanyAndEventDate(any(), any());
        verify(eventMapper, never()).mapToDtos(anyList());
        assertThat(expectedException)
                .isNotNull()
                .isInstanceOf(IllegalArgumentException.class);
    }
}