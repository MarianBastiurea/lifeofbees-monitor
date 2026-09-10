package com.lifeofbees.monitor;

import org.springframework.stereotype.Service;

@Service
public class AlertService {

    public void siteDown(WebsiteStatus status) {
        System.out.println(
                "ALERT: Website is DOWN! Status code: "
                        + status.statusCode()
        );
    }

    public void siteRecovered(WebsiteStatus status) {
        System.out.println(
                "RECOVERY: Website is back UP! Status code: "
                        + status.statusCode()
        );
    }
}