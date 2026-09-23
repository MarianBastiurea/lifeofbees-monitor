package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringEventTest {

    @Test
    void shouldCreateMonitoringEvent() {

        LocalDateTime timestamp = LocalDateTime.now();
        List<String> aiActions = List.of( "CheckApplicationStatus: Container 'spring-boot-app-new' status: " +
                "running", "ReadApplicationLog: Application started successfully",
                "RestartApplication: Container 'spring-boot-app-new' restarted successfully." );
        MonitoringEvent event = new MonitoringEvent(
                timestamp,
                502,
                false,
                "Bad Gateway - the web server cannot reach the application",
                "AI investigated the application and restarted the container. Website recovered.",
                true,
                aiActions
        );

        assertEquals(timestamp, event.timestamp());
        assertEquals(502, event.statusCode());
        assertFalse(event.available());

        assertEquals(
                "Bad Gateway - the web server cannot reach the application",
                event.diagnosis()
        );

        assertEquals(
                "AI investigated the application and restarted the container. Website recovered.",
                event.aiReport()
        );

        assertTrue(event.websiteRecovered());
    }


    @Test
    void showMongoUri() {

        String uri = System.getenv("MONGODB_URI");

        System.out.println("=================================");
        System.out.println("MONGODB_URI = [" + uri + "]");
        System.out.println("=================================");
    }

}

