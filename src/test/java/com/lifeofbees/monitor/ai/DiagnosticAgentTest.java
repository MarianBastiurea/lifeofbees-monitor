package com.lifeofbees.monitor.ai;

import com.lifeofbees.monitor.monitoring.WebsiteStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiagnosticAgentTest {

    private final OpenAiDiagnosticService openAiDiagnosticService =
            mock(OpenAiDiagnosticService.class);

    private final DiagnosticAgent diagnosticAgent =
            new DiagnosticAgent(openAiDiagnosticService);

    @Test
    void shouldIdentifyHealthyWebsite() {

        WebsiteStatus status = new WebsiteStatus(200, true);

        DiagnosticResult result =
                diagnosticAgent.diagnose(status);

        assertTrue(result.healthy());
        assertEquals(
                "Website is responding normally",
                result.reason()
        );

        verifyNoInteractions(openAiDiagnosticService);
    }

    @Test
    void shouldUseAiForBadGateway() {

        WebsiteStatus status = new WebsiteStatus(502, false);

        when(openAiDiagnosticService.diagnose(status))
                .thenReturn("AI diagnosis for Bad Gateway");

        DiagnosticResult result =
                diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "AI diagnosis for Bad Gateway",
                result.reason()
        );

        verify(openAiDiagnosticService).diagnose(status);
    }

    @Test
    void shouldUseAiForServiceUnavailable() {

        WebsiteStatus status = new WebsiteStatus(503, false);

        when(openAiDiagnosticService.diagnose(status))
                .thenReturn("AI diagnosis for Service Unavailable");

        DiagnosticResult result =
                diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "AI diagnosis for Service Unavailable",
                result.reason()
        );

        verify(openAiDiagnosticService).diagnose(status);
    }

    @Test
    void shouldUseAiForGatewayTimeout() {

        WebsiteStatus status = new WebsiteStatus(504, false);

        when(openAiDiagnosticService.diagnose(status))
                .thenReturn("AI diagnosis for Gateway Timeout");

        DiagnosticResult result =
                diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "AI diagnosis for Gateway Timeout",
                result.reason()
        );

        verify(openAiDiagnosticService).diagnose(status);
    }

    @Test
    void shouldUseAiWhenThereIsNoHttpResponse() {

        WebsiteStatus status = new WebsiteStatus(0, false);

        when(openAiDiagnosticService.diagnose(status))
                .thenReturn("AI diagnosis when there is no HTTP response");

        DiagnosticResult result =
                diagnosticAgent.diagnose(status);

        assertFalse(result.healthy());
        assertEquals(
                "AI diagnosis when there is no HTTP response",
                result.reason()
        );

        verify(openAiDiagnosticService).diagnose(status);
    }
}