package com.lifeofbees.monitor;

public record WebsiteStatus(
        int statusCode,
        boolean available
) {
}