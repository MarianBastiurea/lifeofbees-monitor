package com.lifeofbees.monitor;

import java.time.LocalDateTime;

public record MonitoringEvent(
        LocalDateTime timestamp,
        int statusCode,
        boolean available,
        String diagnosis
) {
}