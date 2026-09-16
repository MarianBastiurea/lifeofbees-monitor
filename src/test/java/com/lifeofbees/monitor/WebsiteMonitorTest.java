package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebsiteMonitorTest {

    @Test
    void shouldReturnWebsiteStatusFromChecker() {

        WebsiteChecker websiteChecker = mock(WebsiteChecker.class);
        AlertService   alertService=mock(AlertService.class);
        DiagnosticAgent diagnosticAgent = mock(DiagnosticAgent.class);
        MonitoringEventRepository monitoringEventRepository =
                mock(MonitoringEventRepository.class);

        WebsiteMonitor websiteMonitor = new WebsiteMonitor(websiteChecker,alertService,diagnosticAgent,monitoringEventRepository);

        WebsiteStatus expectedStatus = new WebsiteStatus(200, true);

        when(websiteChecker.check("https://bbc.co.uk"))
                .thenReturn(expectedStatus);

        WebsiteStatus actualStatus =
                websiteMonitor.monitor("https://bbc.co.uk");

        assertEquals(expectedStatus, actualStatus);

        verify(websiteChecker).check("https://bbc.co.uk");
    }

    @Test
    void shouldCreateMonitoringEvent() {

        WebsiteChecker websiteChecker = mock(WebsiteChecker.class);
        AlertService alertService = mock(AlertService.class);
        DiagnosticAgent diagnosticAgent = mock(DiagnosticAgent.class);
        MonitoringEventRepository monitoringEventRepository =
                mock(MonitoringEventRepository.class);

        WebsiteMonitor websiteMonitor =
                new WebsiteMonitor(
                        websiteChecker,
                        alertService,
                        diagnosticAgent,
                        monitoringEventRepository
                );

        WebsiteStatus status = new WebsiteStatus(502, false);

        DiagnosticResult diagnostic = new DiagnosticResult(
                false,
                "Bad Gateway - the web server cannot reach the application"
        );

        when(diagnosticAgent.diagnose(status))
                .thenReturn(diagnostic);

        MonitoringEvent event =
                websiteMonitor.createEvent(status, diagnostic);

        assertEquals(502, event.statusCode());
        assertFalse(event.available());
        assertEquals(
                "Bad Gateway - the web server cannot reach the application",
                event.diagnosis()
        );
        assertNotNull(event.timestamp());
    }
}

