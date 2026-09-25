package com.lifeofbees.monitor.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;


@Repository
public interface MonitoringEventRepository
        extends MongoRepository<MonitoringEvent, String> {
}