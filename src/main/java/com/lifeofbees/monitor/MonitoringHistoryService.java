package com.lifeofbees.monitor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitoringHistoryService {

    private final MonitoringEventRepository repository;

    public MonitoringHistoryService(MonitoringEventRepository repository) {
        this.repository = repository;
    }

    public List<MonitoringEvent> getAllEvents() {
        return repository.findAll();
    }

    public MonitoringEvent getLatestEvent() {

        List<MonitoringEvent> events = repository.findAll();

        if (events.isEmpty()) {
            return null;
        }

        return events.get(events.size() - 1);
    }
}