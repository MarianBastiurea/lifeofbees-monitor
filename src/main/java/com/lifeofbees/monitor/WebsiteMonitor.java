package com.lifeofbees.monitor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WebsiteMonitor {

    private final WebsiteChecker websiteChecker;
    private final AlertService alertService;
    private Boolean lastAvailable = null;
    private final DiagnosticAgent diagnosticAgent;
    private final MonitoringEventRepository monitoringEventRepository;
    private final OpenAiToolAgent openAiToolAgent;

    public WebsiteMonitor(
            WebsiteChecker websiteChecker,
            AlertService alertService,
            DiagnosticAgent diagnosticAgent,
            MonitoringEventRepository monitoringEventRepository,
            OpenAiToolAgent openAiToolAgent) {

        this.websiteChecker = websiteChecker;
        this.alertService = alertService;
        this.diagnosticAgent = diagnosticAgent;
        this.monitoringEventRepository = monitoringEventRepository;
        this.openAiToolAgent = openAiToolAgent;
    }

    // just for tests
    public WebsiteStatus monitor(String url) {
        return websiteChecker.check(url);
    }

    public MonitoringEvent createEvent(
            WebsiteStatus status,
            DiagnosticResult diagnostic,
            AiInvestigationResult aiResult) {

        return new MonitoringEvent(
                LocalDateTime.now(),
                status.statusCode(),
                status.available(),
                diagnostic.reason(),
                aiResult != null ? aiResult.report() : null,
                aiResult != null && aiResult.websiteRecovered(),
                aiResult != null ? aiResult.actions() : null
        );
    }

    @Scheduled(cron = "${monitor.cron}")
    public MonitoringEvent checkWebsite() {

        WebsiteStatus status =
                websiteChecker.check("https://lifeofbees.co.uk");

        DiagnosticResult diagnostic;
        AiInvestigationResult aiResult = null;

        if (status.available()) {

            diagnostic =
                    diagnosticAgent.diagnose(status);

        } else {

            diagnostic =
                    new DiagnosticResult(
                            false,
                            "Website is unavailable - AI investigation started"
                    );

            aiResult =
                    openAiToolAgent.investigateWebsite();

            System.out.println("AI INVESTIGATION:");
            System.out.println(aiResult.report());

            System.out.println(
                    "WEBSITE RECOVERED: "
                            + aiResult.websiteRecovered()
            );
        }

        MonitoringEvent event =
                createEvent(status, diagnostic, aiResult);

        monitoringEventRepository.save(event);

        System.out.println(
                "Website status " + event.statusCode()
                        + ", available " + event.available()
                        + ", diagnostic " + event.diagnosis()
        );

        if (lastAvailable == null) {

            lastAvailable = status.available();

            if (!status.available()) {
                alertService.siteDown(status, diagnostic);
            }

            return event;
        }

        if (lastAvailable && !status.available()) {
            alertService.siteDown(status, diagnostic);
        }

        if (!lastAvailable && status.available()) {
            alertService.siteRecovered(status, diagnostic);
        }

        lastAvailable = status.available();
        return event;
    }
}

