package com.lifeofbees.monitor;

import java.time.LocalDateTime;
import java.util.List;

public record MonitoringEvent(
        LocalDateTime timestamp,
        int statusCode,
        boolean available,
        String aiReport,
        boolean websiteRecovered,
        List<String> aiActions
) {
}