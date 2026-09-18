package com.lifeofbees.monitor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//va fi stearsa la sfarsit


@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final MonitoringHistoryService historyService;
    private final WebsiteMonitor websiteMonitor;

    public MonitoringController(MonitoringHistoryService historyService, WebsiteMonitor websiteMonitor) {
        this.historyService = historyService;
        this.websiteMonitor=websiteMonitor;
    }

    @GetMapping("/history")
    public List<MonitoringEvent> getHistory() {
        return historyService.getAllEvents();
    }

    @GetMapping("/latest")
    public MonitoringEvent getLatest() {
        return historyService.getLatestEvent();
    }

}