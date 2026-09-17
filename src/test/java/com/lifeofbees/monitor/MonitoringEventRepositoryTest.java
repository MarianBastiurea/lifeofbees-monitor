package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MonitoringEventRepositoryTest {

    @Autowired
    private MonitoringEventRepository repository;

    @Test
    void shouldSaveAndFindEvent() {

        MonitoringEvent event = new MonitoringEvent(
                LocalDateTime.now(),
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        repository.save(event);

        List<MonitoringEvent> events = repository.findAll();

        assertThat(events).isNotEmpty();
        assertThat(events).contains(event);
    }
}