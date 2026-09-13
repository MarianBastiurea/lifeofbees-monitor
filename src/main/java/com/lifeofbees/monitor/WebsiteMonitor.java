package com.lifeofbees.monitor;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebsiteMonitor {

    private final WebsiteChecker websiteChecker;
    private final AlertService  alertService;
    private Boolean lastAvailable=null;
    private final DiagnosticAgent diagnosticAgent;

    public WebsiteMonitor(WebsiteChecker websiteChecker, AlertService alertService, DiagnosticAgent diagnosticAgent) {
        this.websiteChecker = websiteChecker;
        this.alertService=alertService;
        this.diagnosticAgent=diagnosticAgent;
    }

    public WebsiteStatus monitor(String url) {
        return websiteChecker.check(url);
    }

    @Scheduled(cron = "${monitor.cron}")
    public  void checkWebsite(){
        WebsiteStatus status=websiteChecker.check("https://lifeofbees.co.uk");
        DiagnosticResult diagnostic = diagnosticAgent.diagnose(status);

        System.out.println("Website status "+status.statusCode()+
                ",  available "+status.available()+
                ",  diagnostic"+diagnostic.reason());

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
            alertService.siteRecovered(status,diagnostic);
        }

        lastAvailable = status.available();
    }
}
