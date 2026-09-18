package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MonitoringEventTest {

    @Test
    void shouldCreateMonitoringEvent() {

        LocalDateTime timestamp = LocalDateTime.now();

        MonitoringEvent event = new MonitoringEvent(
                timestamp,
                502,
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        assertEquals(timestamp, event.timestamp());
        assertEquals(502, event.statusCode());
        assertFalse(event.available());
        assertEquals(
                "Bad Gateway - the web server cannot reach the application",
                event.diagnosis()
        );
    }


    @Test
    void showMongoUri() {

        String uri = System.getenv("MONGODB_URI");

        System.out.println("=================================");
        System.out.println("MONGODB_URI = [" + uri + "]");
        System.out.println("=================================");
    }

}