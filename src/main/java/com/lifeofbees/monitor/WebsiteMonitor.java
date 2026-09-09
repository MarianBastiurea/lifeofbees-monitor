package com.lifeofbees.monitor;
import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebsiteMonitor {

    private final WebsiteChecker websiteChecker;

    public WebsiteMonitor(WebsiteChecker websiteChecker) {
        this.websiteChecker = websiteChecker;
    }

    public WebsiteStatus monitor(String url) throws IOException {
        return websiteChecker.check(url);
    }

    @Scheduled(fixedRate=6000)
    public  void checkWebsite(){
        WebsiteStatus status=websiteChecker.check("https://lifeofbees.co.uk");
        System.out.println("Website status "+status.statusCode()+" available "+status.available());

    }
}
