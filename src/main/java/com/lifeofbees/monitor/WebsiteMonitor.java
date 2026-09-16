package com.lifeofbees.monitor;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class WebsiteMonitor {

    private final WebsiteChecker websiteChecker;
    private final AlertService  alertService;
    private Boolean lastAvailable=null;
    private final DiagnosticAgent diagnosticAgent;
    private final MonitoringEventRepository monitoringEventRepository;

    public WebsiteMonitor(
            WebsiteChecker websiteChecker,
            AlertService alertService,
            DiagnosticAgent diagnosticAgent,
            MonitoringEventRepository monitoringEventRepository) {

        this.websiteChecker = websiteChecker;
        this.alertService = alertService;
        this.diagnosticAgent = diagnosticAgent;
        this.monitoringEventRepository = monitoringEventRepository;
    }

    public WebsiteStatus monitor(String url) {
        return websiteChecker.check(url);
    }

    public MonitoringEvent createEvent(
            WebsiteStatus status,
            DiagnosticResult diagnostic) {

        return new MonitoringEvent(
                LocalDateTime.now(),
                status.statusCode(),
                status.available(),
                diagnostic.reason()
        );
    }



    @Scheduled(cron = "${monitor.cron}")
    public void checkWebsite() {

        WebsiteStatus status =
                websiteChecker.check("https://lifeofbees.co.uk");

        DiagnosticResult diagnostic =
                diagnosticAgent.diagnose(status);

        MonitoringEvent event =
                createEvent(status, diagnostic);

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

            return;
        }

        if (lastAvailable && !status.available()) {
            alertService.siteDown(status, diagnostic);
        }

        if (!lastAvailable && status.available()) {
            alertService.siteRecovered(status, diagnostic);
        }

        lastAvailable = status.available();
    }
}
