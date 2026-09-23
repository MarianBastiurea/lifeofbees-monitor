package com.lifeofbees.monitor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WebsiteMonitorAgentTest {

    @Test
    void shouldInvokeAiAgentWhenWebsiteIsDown() {

        WebsiteChecker websiteChecker =
                mock(WebsiteChecker.class);

        AlertService alertService =
                mock(AlertService.class);

        DiagnosticAgent diagnosticAgent =
                mock(DiagnosticAgent.class);

        MonitoringEventRepository repository =
                mock(MonitoringEventRepository.class);

        OpenAiToolAgent openAiToolAgent =
                mock(OpenAiToolAgent.class);

        WebsiteStatus down =
                new WebsiteStatus(502, false);

        DiagnosticResult diagnostic =
                new DiagnosticResult(
                        false,
                        "Bad Gateway"
                );

        when(websiteChecker.check(
                "https://lifeofbees.co.uk"
        )).thenReturn(down);

        when(diagnosticAgent.diagnose(down))
                .thenReturn(diagnostic);

        when(openAiToolAgent.investigateWebsite())
                .thenReturn(
                        new AiInvestigationResult(
                                "...",
                                true,
                                List.of(
                                        "RestartApplication: Container 'spring-boot-app-new' restarted successfully.",
                                        "CheckWebsite: HTTP 200, available=true"
                                )
                        )
                );

        when(repository.save(any(MonitoringEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WebsiteMonitor monitor =
                new WebsiteMonitor(
                        websiteChecker,
                        alertService,
                        diagnosticAgent,
                        repository,
                        openAiToolAgent
                );

        MonitoringEvent event =
                monitor.checkWebsite();

        assertEquals(502, event.statusCode());
        assertEquals(false, event.available());

        verify(openAiToolAgent, times(1))
                .investigateWebsite();
    }

    @Test
    void shouldNotInvokeAiAgentWhenWebsiteIsUp() {

        WebsiteChecker websiteChecker =
                mock(WebsiteChecker.class);

        AlertService alertService =
                mock(AlertService.class);

        DiagnosticAgent diagnosticAgent =
                mock(DiagnosticAgent.class);

        MonitoringEventRepository repository =
                mock(MonitoringEventRepository.class);

        OpenAiToolAgent openAiToolAgent =
                mock(OpenAiToolAgent.class);

        WebsiteStatus up =
                new WebsiteStatus(200, true);

        DiagnosticResult diagnostic =
                new DiagnosticResult(
                        true,
                        "Website is responding normally"
                );

        when(websiteChecker.check(
                "https://lifeofbees.co.uk"
        )).thenReturn(up);

        when(diagnosticAgent.diagnose(up))
                .thenReturn(diagnostic);

        when(repository.save(any(MonitoringEvent.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WebsiteMonitor monitor =
                new WebsiteMonitor(
                        websiteChecker,
                        alertService,
                        diagnosticAgent,
                        repository,
                        openAiToolAgent
                );

        MonitoringEvent event =
                monitor.checkWebsite();

        assertEquals(200, event.statusCode());
        assertEquals(true, event.available());

        verify(openAiToolAgent, never())
                .investigateWebsite();
    }
}