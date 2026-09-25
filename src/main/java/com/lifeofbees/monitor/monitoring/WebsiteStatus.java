package com.lifeofbees.monitor.monitoring;

public record WebsiteStatus(
        int statusCode,
        boolean available
) {
}