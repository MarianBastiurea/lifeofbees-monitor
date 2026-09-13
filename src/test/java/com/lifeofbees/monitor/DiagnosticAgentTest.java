package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiagnosticAgentTest {

    private final DiagnosticAgent diagnosticAgent = new DiagnosticAgent();

    @Test
    void shouldIdentifyHealthyWebsite() {

        WebsiteStatus status = new WebsiteStatus(200, true);

        DiagnosticResult result = diagnosticAgent.diagnose(status);

        assertTrue(result.healthy());
        assertEquals(
                "Website is responding normally",
                result.reason()
        );
    }

    @Test
    void shouldIdentifyBadGateway() {

        WebsiteStatus status = new WebsiteStatus(502, false);

        DiagnosticResult result = diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "Bad Gateway - the web server cannot reach the application",
                result.reason()
        );
    }

    @Test
    void shouldIdentifyServiceUnavailable() {

        WebsiteStatus status = new WebsiteStatus(503, false);

        DiagnosticResult result = diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "Service Unavailable - the application or server is unavailable",
                result.reason()
        );
    }

    @Test
    void shouldIdentifyGatewayTimeout() {

        WebsiteStatus status = new WebsiteStatus(504, false);

        DiagnosticResult result = diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "Gateway Timeout - the application did not respond in time",
                result.reason()
        );
    }
}