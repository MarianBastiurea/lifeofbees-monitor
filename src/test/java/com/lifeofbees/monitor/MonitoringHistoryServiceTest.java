package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MonitoringHistoryServiceTest {

    @Test
    void shouldReturnAllMonitoringEvents() {

        MonitoringEventRepository repository =
                mock(MonitoringEventRepository.class);

        MonitoringHistoryService service =
                new MonitoringHistoryService(repository);

        MonitoringEvent event1 = new MonitoringEvent(
                LocalDateTime.now(),
                200,
                true,
                "Website is responding normally"
        );

        MonitoringEvent event2 = new MonitoringEvent(
                LocalDateTime.now(),
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        when(repository.findAll())
                .thenReturn(List.of(event1, event2));

        List<MonitoringEvent> events =
                service.getAllEvents();

        assertEquals(2, events.size());
        assertEquals(event1, events.get(0));
        assertEquals(event2, events.get(1));

        verify(repository).findAll();
    }

    @Test
    void shouldReturnLatestMonitoringEvent() {

        MonitoringEventRepository repository =
                mock(MonitoringEventRepository.class);

        MonitoringHistoryService service =
                new MonitoringHistoryService(repository);

        MonitoringEvent event1 = new MonitoringEvent(
                LocalDateTime.now(),
                200,
                true,
                "Website is responding normally"
        );

        MonitoringEvent event2 = new MonitoringEvent(
                LocalDateTime.now(),
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        when(repository.findAll())
                .thenReturn(List.of(event1, event2));

        MonitoringEvent latest =
                service.getLatestEvent();

        assertEquals(event2, latest);

        verify(repository).findAll();
    }

    @Test
    void shouldReturnNullWhenThereAreNoMonitoringEvents() {

        MonitoringEventRepository repository =
                mock(MonitoringEventRepository.class);

        MonitoringHistoryService service =
                new MonitoringHistoryService(repository);

        when(repository.findAll())
                .thenReturn(List.of());

        MonitoringEvent latest =
                service.getLatestEvent();

        assertNull(latest);

        verify(repository).findAll();
    }
}