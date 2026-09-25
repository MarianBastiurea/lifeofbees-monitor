package com.lifeofbees.monitor.persistence;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringEventTest {

    @Test
    void shouldCreateMonitoringEvent() {

        LocalDateTime timestamp = LocalDateTime.now();
        List<String> aiActions = List.of("CheckApplicationStatus: Container 'spring-boot-app-new' status: " +
                        "running", "ReadApplicationLog: Application started successfully",
                "RestartApplication: Container 'spring-boot-app-new' restarted successfully.");
        MonitoringEvent event = new MonitoringEvent(
                timestamp,
                502,
                false,
                "AI investigated the application and restarted the container. Website recovered.",
                true,
                aiActions
        );

        assertEquals(timestamp, event.timestamp());
        assertEquals(502, event.statusCode());
        assertFalse(event.available());

        assertEquals(
                "AI investigated the application and restarted the container. Website recovered.",
                event.aiReport()
        );

        assertTrue(event.websiteRecovered());
    }
}

