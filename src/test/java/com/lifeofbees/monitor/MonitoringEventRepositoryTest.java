package com.lifeofbees.monitor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MonitoringEventRepositoryTest {

    @Autowired
    private MonitoringEventRepository repository;

    @MockitoBean
    private EmailService emailService;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

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

        assertThat(events).hasSize(1);

        MonitoringEvent savedEvent = events.get(0);

        assertThat(savedEvent.statusCode()).isEqualTo(event.statusCode());
        assertThat(savedEvent.available()).isEqualTo(event.available());
        assertThat(savedEvent.diagnosis()).isEqualTo(event.diagnosis());
        assertThat(savedEvent.timestamp()).isEqualTo(
                event.timestamp().withNano(
                        (event.timestamp().getNano() / 1_000_000) * 1_000_000
                )
        );
    }
}