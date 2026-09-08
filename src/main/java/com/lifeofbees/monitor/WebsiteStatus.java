package main.java.com.lifeofbees.monitor;

public record WebsiteStatus(
        String url,
        boolean available,
        int httpStatusCode,
        long responseTimeMs
) {
}