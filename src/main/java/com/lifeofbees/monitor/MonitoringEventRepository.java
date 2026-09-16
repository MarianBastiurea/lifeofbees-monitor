package com.lifeofbees.monitor;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MonitoringEventRepository {

    private final List<MonitoringEvent> events = new ArrayList<>();

    public void save(MonitoringEvent event) {
        events.add(event);
    }

    public List<MonitoringEvent> findAll() {
        return List.copyOf(events);
    }
}