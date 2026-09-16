package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MonitoringEventRepositoryTest {

    @Test
    void shouldSaveAndReturnMonitoringEvent() {

        MonitoringEventRepository repository =
                new MonitoringEventRepository();

        MonitoringEventRepository monitoringEventRepository =
                mock(MonitoringEventRepository.class);
        MonitoringEvent event = new MonitoringEvent(
                LocalDateTime.now(),
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        repository.save(event);

        List<MonitoringEvent> events = repository.findAll();

        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }
}