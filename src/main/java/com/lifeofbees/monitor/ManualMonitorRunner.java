package com.lifeofbees.monitor;

import com.lifeofbees.monitor.monitoring.WebsiteMonitor;
import com.lifeofbees.monitor.persistence.MonitoringEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ManualMonitorRunner implements CommandLineRunner {

    private final WebsiteMonitor websiteMonitor;

    public ManualMonitorRunner(WebsiteMonitor websiteMonitor) {
        this.websiteMonitor = websiteMonitor;
    }

    @Override
    public void run(String... args) {

        for (String arg : args) {

            if ("--monitor.run-on-startup=true".equals(arg)) {

                System.out.println("MANUAL MONITOR RUN STARTED");

                MonitoringEvent event =
                        websiteMonitor.checkWebsite();

                System.out.println(
                        "MANUAL MONITOR RUN COMPLETED"
                );

                System.out.println(
                        "Result: HTTP "
                                + event.statusCode()
                                + ", available="
                                + event.available()
                );

                return;
            }
        }
    }
}

