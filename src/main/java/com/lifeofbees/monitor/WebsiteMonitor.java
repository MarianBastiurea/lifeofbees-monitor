package com.lifeofbees.monitor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WebsiteMonitor {

    private final WebsiteChecker websiteChecker;
    private final AlertService alertService;
    private final MonitoringEventRepository monitoringEventRepository;
    private final OpenAiToolAgent openAiToolAgent;

    public WebsiteMonitor(
            WebsiteChecker websiteChecker,
            AlertService alertService,
            MonitoringEventRepository monitoringEventRepository,
            OpenAiToolAgent openAiToolAgent) {

        this.websiteChecker = websiteChecker;
        this.alertService = alertService;
        this.monitoringEventRepository = monitoringEventRepository;
        this.openAiToolAgent = openAiToolAgent;
    }

    // just for tests
    public WebsiteStatus monitor(String url) {
        return websiteChecker.check(url);
    }

    public MonitoringEvent createEvent(
            WebsiteStatus status,
            AiInvestigationResult aiResult) {

        return new MonitoringEvent(
                LocalDateTime.now(),
                status.statusCode(),
                status.available(),
                aiResult != null ? aiResult.report() : null,
                aiResult != null && aiResult.websiteRecovered(),
                aiResult != null ? aiResult.actions() : null
        );
    }

    @Scheduled(cron = "${monitor.cron}")
    public MonitoringEvent checkWebsite() {

        WebsiteStatus status =
                websiteChecker.check("https://lifeofbees.co.uk");

        AiInvestigationResult aiResult = null;

        if (!status.available()) {

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
                createEvent(status, aiResult);

        monitoringEventRepository.save(event);

        System.out.println(
                "Website status " + event.statusCode()
                        + ", available " + event.available()
        );

        alertService.sendDailyReport(
                status,
                aiResult
        );

        return event;
    }
}

