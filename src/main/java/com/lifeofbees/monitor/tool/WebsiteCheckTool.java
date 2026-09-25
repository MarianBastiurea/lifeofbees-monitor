package com.lifeofbees.monitor.tool;

import com.lifeofbees.monitor.monitoring.WebsiteChecker;
import com.lifeofbees.monitor.monitoring.WebsiteStatus;
import org.springframework.stereotype.Component;

@Component
public class WebsiteCheckTool {

    private final WebsiteChecker websiteChecker;

    public WebsiteCheckTool(WebsiteChecker websiteChecker) {
        this.websiteChecker = websiteChecker;
    }

    public WebsiteStatus checkWebsite() {
        return websiteChecker.check("https://lifeofbees.co.uk");
    }
}