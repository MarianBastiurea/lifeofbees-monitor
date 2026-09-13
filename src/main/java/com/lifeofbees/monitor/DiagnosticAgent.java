package com.lifeofbees.monitor;

import org.springframework.stereotype.Service;

@Service
public class DiagnosticAgent {

    public DiagnosticResult diagnose(WebsiteStatus status) {

        if (status.available()) {
            return new DiagnosticResult(
                    true,
                    "Website is responding normally"
            );
        }

        if (status.statusCode() == 502) {
            return new DiagnosticResult(
                    false,
                    "Bad Gateway - the web server cannot reach the application"
            );
        }

        if (status.statusCode() == 503) {
            return new DiagnosticResult(
                    false,
                    "Service Unavailable - the application or server is unavailable"
            );
        }

        if (status.statusCode() == 504) {
            return new DiagnosticResult(
                    false,
                    "Gateway Timeout - the application did not respond in time"
            );
        }

        return new DiagnosticResult(
                false,
                "Website is unavailable - HTTP status "
                        + status.statusCode()
        );
    }
}