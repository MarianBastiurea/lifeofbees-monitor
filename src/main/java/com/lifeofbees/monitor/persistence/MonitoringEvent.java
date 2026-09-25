package com.lifeofbees.monitor.persistence;

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