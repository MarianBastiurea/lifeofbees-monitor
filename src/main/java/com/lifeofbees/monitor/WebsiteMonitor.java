package com.lifeofbees.monitor;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebsiteMonitor {

    private final WebsiteChecker websiteChecker;
    private final AlertService  alertService;
    private Boolean lastAvailable=null;

    public WebsiteMonitor(WebsiteChecker websiteChecker, AlertService alertService) {
        this.websiteChecker = websiteChecker;
        this.alertService=alertService;
    }

    public WebsiteStatus monitor(String url) {
        return websiteChecker.check(url);
    }

    @Scheduled(cron = "${monitor.cron}")
    public  void checkWebsite(){
        WebsiteStatus status=websiteChecker.check("https://lifeofbees.co.uk");
        System.out.println("Website status "+status.statusCode()+" available "+status.available());

        if (lastAvailable == null) {

            lastAvailable = status.available();

            if (!status.available()) {
                alertService.siteDown(status);
            }

            return;
        }

        if (lastAvailable && !status.available()) {
            alertService.siteDown(status);
        }

        if (!lastAvailable && status.available()) {
            alertService.siteRecovered(status);
        }

        lastAvailable = status.available();
    }
}
